package com.crossoverjie.cim.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.GroupReqVO;
import com.crossoverjie.cim.client.vo.req.LoginReqVO;
import com.crossoverjie.cim.client.vo.req.P2PReqVO;
import com.crossoverjie.cim.client.vo.res.CIMServerResVO;
import com.crossoverjie.cim.client.vo.res.OnlineUsersResVO;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/22 22:27
 * @since JDK 1.8
 */
@Service
public class RouteRequestImpl implements RouteRequest {

    private final static Logger LOGGER = LoggerFactory.getLogger(RouteRequestImpl.class);

    @Autowired
    private OkHttpClient okHttpClient ;

    private MediaType mediaType = MediaType.parse("application/json");

    @Value("${cim.group.route.request.url}")
    private String groupRouteRequestUrl;

    @Value("${cim.p2p.route.request.url}")
    private String p2pRouteRequestUrl;

    @Value("${cim.server.route.request.url}")
    private String serverRouteLoginUrl;

    @Value("${cim.server.online.user.url}")
    private String onlineUserUrl;



    @Autowired
    private AppConfiguration appConfiguration ;

    @Override
    public void sendGroupMsg(GroupReqVO groupReqVO) throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",groupReqVO.getMsg());
        jsonObject.put("userId",groupReqVO.getUserId());
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(groupRouteRequestUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        try {
            if (!response.isSuccessful()){
                throw new IOException("Unexpected code " + response);
            }
        }finally {
            response.body().close();
        }
    }

    @Override
    public void sendP2PMsg(P2PReqVO p2PReqVO) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",p2PReqVO.getMsg());
        jsonObject.put("userId",p2PReqVO.getUserId());
        jsonObject.put("receiveUserId",p2PReqVO.getReceiveUserId());
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(p2pRouteRequestUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }

        ResponseBody body = response.body();
        try {
            String json = body.string() ;
            BaseResponse baseResponse = JSON.parseObject(json, BaseResponse.class);

            //选择的账号不存在
            if (baseResponse.getCode().equals(StatusEnum.OFF_LINE.getCode())){
                LOGGER.error(p2PReqVO.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
            }

        }finally {
            body.close();
        }
    }

    @Override
    public CIMServerResVO.ServerInfo getCIMServer(LoginReqVO loginReqVO) throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId",loginReqVO.getUserId());
        jsonObject.put("userName",loginReqVO.getUserName());
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(serverRouteLoginUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }
        CIMServerResVO cimServerResVO ;
        ResponseBody body = response.body();
        try {
            String json = body.string();
            cimServerResVO = JSON.parseObject(json, CIMServerResVO.class);

            //重复失败
            if (!cimServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())){
                LOGGER.error(appConfiguration.getUserName() + ":" + cimServerResVO.getMessage());
                System.exit(-1);
            }

        }finally {
            body.close();
        }



        return cimServerResVO.getDataBody();
    }

    @Override
    public List<OnlineUsersResVO.DataBodyBean> onlineUsers() throws Exception{

        JSONObject jsonObject = new JSONObject();
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(onlineUserUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }


        ResponseBody body = response.body();
        OnlineUsersResVO onlineUsersResVO ;
        try {
            String json = body.string() ;
            onlineUsersResVO = JSON.parseObject(json, OnlineUsersResVO.class);

        }finally {
            body.close();
        }

        return onlineUsersResVO.getDataBody();
    }

    @Override
    public void offLine() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", appConfiguration.getUserId());
        jsonObject.put("msg", "offLine");
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(appConfiguration.getClearRouteUrl())
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            LOGGER.error("exception",e);
        } finally {
            response.body().close();
        }
    }
}
