package com.crossoverjie.cim.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.GroupReqVO;
import com.crossoverjie.cim.client.vo.req.LoginReqVO;
import com.crossoverjie.cim.client.vo.res.CIMServerResVO;
import com.crossoverjie.cim.common.enums.StatusEnum;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    @Value("${cim.server.route.request.url}")
    private String serverRouteRequestUrl;

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
        if (!cimServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())){
            throw new RuntimeException("route server exception code=" + cimServerResVO.getCode()) ;
        }

        return cimServerResVO.getDataBody();
    }
}
