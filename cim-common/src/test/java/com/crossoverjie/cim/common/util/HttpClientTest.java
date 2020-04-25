package com.crossoverjie.cim.common.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClientTest {

    private OkHttpClient okHttpClient ;

    @Before
    public void before(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        okHttpClient = builder.build();
    }

    @Test
    public void call() throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "hello");
        jsonObject.put("userId", 1586617710861L);

        HttpClient.call(okHttpClient,jsonObject.toString(),"http://127.0.0.1:8081/sendMsg") ;
    }
}