package com.crossoverjie.cim.client.sdk.impl;

import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.sdk.io.backoff.BackoffStrategy;
import com.crossoverjie.cim.client.sdk.io.MessageListener;
import com.crossoverjie.cim.client.sdk.io.backoff.RandomBackoff;
import com.crossoverjie.cim.client.sdk.io.ReconnectCheck;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.OkHttpClient;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientConfigurationData {

    private Auth auth;

    @Data
    @AllArgsConstructor
    @Builder
    public static class Auth{
        private long userId;
        private String userName;
    }

    private String routeUrl;
    private int loginRetryCount = 5;

    @JsonIgnore
    private Event event = new Event.DefaultEvent();

    @JsonIgnore
    private MessageListener messageListener =
            (client, properties, msg) -> System.out.printf("id:[%s] msg:[%s]%n \n", client.getAuth(), msg);

    @JsonIgnore
    private OkHttpClient okHttpClient = new OkHttpClient();

    @JsonIgnore
    private ThreadPoolExecutor callbackThreadPool;

    @JsonIgnore
    private ReconnectCheck reconnectCheck = (client) -> true;

    @JsonIgnore
    private BackoffStrategy backoffStrategy = new RandomBackoff();
}
