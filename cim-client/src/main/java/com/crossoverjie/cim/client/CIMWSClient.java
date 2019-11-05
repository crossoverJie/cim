package com.crossoverjie.cim.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.client.vo.res.CIMServerResVO;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.req.WebSocketRequest;
import okhttp3.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * websocket测试客户端
 * @author georgeYang
 */
public class CIMWSClient {
    private static final MediaType mediaType = MediaType.parse("application/json");

    private static final String groupRouteUrl = "http://localhost:8800";
    private static final Long userId = 1572947580784L;
    private static final String username = "1";
    private static WebSocketRequest loginRequest = null;
    private static WebSocketRequest heartBeatRequest = null;
    private static WebSocketSession webSocketSession;


    public static void main(String[] args) {
        //开始登入
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        logout(okHttpClient,userId);

        String ws_uri = login(okHttpClient,userId,username);
        if (ws_uri == null) {
            System.err.println("错误");
            System.exit(0);
            return;
        }


        loginRequest = new WebSocketRequest();
        loginRequest.setType(Constants.CommandType.LOGIN);
        loginRequest.setRequestId(userId);
        loginRequest.setReqMsg(username);


        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, new MyHandler(), ws_uri);
        manager.start();

        heartBeatRequest = new WebSocketRequest();
        heartBeatRequest.setReqMsg("ping");
        heartBeatRequest.setRequestId(0L);
        heartBeatRequest.setType(Constants.CommandType.PING);

        while (true) {
            try {
                Thread.sleep(10000);
                if (webSocketSession != null)
                    webSocketSession.sendMessage(new TextMessage(JSONObject.toJSONString(heartBeatRequest)));
            } catch (Exception e) {
            }
        }
    }

    private static void logout(OkHttpClient okHttpClient, Long userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reqNo", "1234567890");
        jsonObject.put("timeStamp", "0");
        jsonObject.put("userId", userId);
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(groupRouteUrl + "/offLine")
                .post(requestBody)
                .build();

        ResponseBody body = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            body = response.body();
            String json = body.string();
            System.out.println("退出登录json:" + json);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                body.close();
            } catch (Exception e) {
            }
        }
    }

    private static String login(OkHttpClient okHttpClient, Long userId, String username) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reqNo", "1234567890");
        jsonObject.put("timeStamp", "0");
        jsonObject.put("userId", userId);
        jsonObject.put("userName", username);
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(groupRouteUrl + "/login")
                .post(requestBody)
                .build();

        String ws_uri = null;
        ResponseBody body = null;
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            body = response.body();
            String json = body.string();
            System.out.println("json:" + json);
            CIMServerResVO cimServerResVO = JSON.parseObject(json, CIMServerResVO.class);
            CIMServerResVO.ServerInfo serverInfo = cimServerResVO.getDataBody();
            ws_uri = "ws://" + serverInfo.getIp() + ":" + serverInfo.getCimServerPort() + "/websocket";
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                body.close();
            } catch (Exception e) {
            }
        }
        return ws_uri;
    }

    private static class MyHandler extends AbstractWebSocketHandler {
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            System.out.println("connected...........");
            webSocketSession = session;
            session.sendMessage(new TextMessage(JSONObject.toJSONString(loginRequest)));
            super.afterConnectionEstablished(session);
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message)
                throws Exception {
            webSocketSession = session;
            System.out.println("receive: " + message.getPayload());
            super.handleTextMessage(session, message);
        }
    }
}