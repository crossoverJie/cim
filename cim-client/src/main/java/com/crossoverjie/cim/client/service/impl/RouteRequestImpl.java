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
    private String serverRouteRequestUrl;

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
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
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

        String json = response.body().string() ;
        BaseResponse baseResponse = JSON.parseObject(json, BaseResponse.class);

        //选择的账号不存在
        if (baseResponse.getCode().equals(StatusEnum.OFF_LINE.getCode())){
            LOGGER.error(p2PReqVO.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
        }
    }

    @Override
    public CIMServerResVO.ServerInfo getCIMServer(LoginReqVO loginReqVO) throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId",loginReqVO.getUserId());
        jsonObject.put("userName",loginReqVO.getUserName());
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(serverRouteRequestUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }

        String json = response.body().string();
        CIMServerResVO cimServerResVO = JSON.parseObject(json, CIMServerResVO.class);

        //重复登录
        if (cimServerResVO.getCode().equals(StatusEnum.REPEAT_LOGIN.getCode())){
            LOGGER.error(appConfiguration.getUserName() + ":" + StatusEnum.REPEAT_LOGIN.getMessage());
            System.exit(-1);
        }

        if (!cimServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())){
            throw new RuntimeException("route server exception code=" + cimServerResVO.getCode()) ;
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


        String json = response.body().string() ;
        OnlineUsersResVO onlineUsersResVO = JSON.parseObject(json, OnlineUsersResVO.class);

        return onlineUsersResVO.getDataBody();
    }
}
