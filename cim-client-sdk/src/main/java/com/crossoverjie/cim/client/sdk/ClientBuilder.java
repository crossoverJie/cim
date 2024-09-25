package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.client.sdk.io.MessageListener;
import com.crossoverjie.cim.client.sdk.io.ReconnectCheck;
import java.util.concurrent.ThreadPoolExecutor;

import com.crossoverjie.cim.client.sdk.io.backoff.BackoffStrategy;
import okhttp3.OkHttpClient;

/**
 * @author crossoverJie
 */
public interface ClientBuilder {

    Client build();
    ClientBuilder auth(ClientConfigurationData.Auth auth);
    ClientBuilder routeUrl(String routeUrl);
    ClientBuilder loginRetryCount(int loginRetryCount);
    ClientBuilder event(Event event);
    ClientBuilder reconnectCheck(ReconnectCheck reconnectCheck);
    ClientBuilder okHttpClient(OkHttpClient okHttpClient);
    ClientBuilder messageListener(MessageListener messageListener);
    ClientBuilder callbackThreadPool(ThreadPoolExecutor callbackThreadPool);
    ClientBuilder backoffStrategy(BackoffStrategy backoffStrategy);
}
