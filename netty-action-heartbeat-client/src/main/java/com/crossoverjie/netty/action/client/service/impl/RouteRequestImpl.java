package com.crossoverjie.netty.action.client.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.netty.action.client.service.RouteRequest;
import okhttp3.*;
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

    @Autowired
    private OkHttpClient okHttpClient ;


    private MediaType mediaType = MediaType.parse("application/json");

    @Value("${cim.route.request.url}")
    private String routeRequestUrl ;

    @Override
    public void sendGroupMsg(String msg) throws Exception {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg",msg);
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());

        Request request = new Request.Builder()
                .url(routeRequestUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute() ;
        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }
    }
}
