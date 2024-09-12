package com.crossoverjie.cim.client.sdk.impl;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.ClientBuilder;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.sdk.io.MessageListener;
import com.crossoverjie.cim.client.sdk.io.ReconnectCheck;
import java.util.concurrent.ThreadPoolExecutor;
import okhttp3.OkHttpClient;

public class ClientBuilderImpl implements ClientBuilder {


    private ClientConfigurationData conf;

    public ClientBuilderImpl() {
        this(new ClientConfigurationData());
    }
    public ClientBuilderImpl(ClientConfigurationData conf) {
        this.conf = conf;
    }
    
    @Override
    public Client build() {
        return new ClientImpl(conf);
    }

    @Override
    public ClientBuilder userId(Long userId) {
        this.conf.setUserId(userId);
        return this;
    }

    @Override
    public ClientBuilder userName(String userName) {
        this.conf.setUserName(userName);
        return this;
    }

    @Override
    public ClientBuilder routeUrl(String routeUrl) {
        this.conf.setRouteUrl(routeUrl);
        return this;
    }

    @Override
    public ClientBuilder loginRetryCount(int loginRetryCount) {
        this.conf.setLoginRetryCount(loginRetryCount);
        return this;
    }

    @Override
    public ClientBuilder event(Event event) {
        this.conf.setEvent(event);
        return this;
    }

    @Override
    public ClientBuilder reconnectCheck(ReconnectCheck reconnectCheck) {
        this.conf.setReconnectCheck(reconnectCheck);
        return this;
    }

    @Override
    public ClientBuilder okHttpClient(OkHttpClient okHttpClient) {
        this.conf.setOkHttpClient(okHttpClient);
        return this;
    }

    @Override
    public ClientBuilder messageListener(MessageListener messageListener) {
        this.conf.setMessageListener(messageListener);
        return this;
    }

    @Override
    public ClientBuilder callbackThreadPool(ThreadPoolExecutor callbackThreadPool) {
        this.conf.setCallbackThreadPool(callbackThreadPool);
        return this;
    }
}
