package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.io.MessageListener;
import com.crossoverjie.cim.client.sdk.io.ReconnectCheck;
import java.util.concurrent.ThreadPoolExecutor;
import okhttp3.OkHttpClient;

public interface ClientBuilder {

    Client build();

    ClientBuilder userId(Long userId);
    ClientBuilder userName(String userName);
    ClientBuilder routeUrl(String routeUrl);
    ClientBuilder loginRetryCount(int loginRetryCount);
    ClientBuilder event(Event event);
    ClientBuilder reconnectCheck(ReconnectCheck reconnectCheck);

    ClientBuilder okHttpClient(OkHttpClient okHttpClient);
    ClientBuilder messageListener(MessageListener messageListener);
    ClientBuilder callbackThreadPool(ThreadPoolExecutor callbackThreadPool);
}
