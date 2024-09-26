package com.crossoverjie.cim.client.sdk.impl;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.ClientBuilder;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.sdk.io.MessageListener;
import com.crossoverjie.cim.client.sdk.io.ReconnectCheck;
import com.crossoverjie.cim.client.sdk.io.backoff.BackoffStrategy;
import com.crossoverjie.cim.common.util.StringUtil;
import java.util.concurrent.ThreadPoolExecutor;
import okhttp3.OkHttpClient;

public class ClientBuilderImpl implements ClientBuilder {


    private final ClientConfigurationData conf;

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
    public ClientBuilder auth(ClientConfigurationData.Auth auth) {
        if (auth.getUserId() <= 0 || StringUtil.isEmpty(auth.getUserName())){
            throw new IllegalArgumentException("userId and userName must be set");
        }
        this.conf.setAuth(auth);
        return this;
    }

    @Override
    public ClientBuilder routeUrl(String routeUrl) {
        if (StringUtil.isEmpty(routeUrl)) {
            throw new IllegalArgumentException("routeUrl must be set");
        }
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

    @Override
    public ClientBuilder backoffStrategy(BackoffStrategy backoffStrategy) {
        this.conf.setBackoffStrategy(backoffStrategy);
        return this;
    }
}
