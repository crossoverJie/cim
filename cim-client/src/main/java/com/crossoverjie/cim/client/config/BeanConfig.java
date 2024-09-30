package com.crossoverjie.cim.client.config;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.client.sdk.io.backoff.RandomBackoff;
import com.crossoverjie.cim.client.service.MsgLogger;
import com.crossoverjie.cim.client.service.ShutDownSign;
import com.crossoverjie.cim.client.service.impl.MsgCallBackListener;
import com.crossoverjie.cim.common.data.construct.RingBufferWheel;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Function:bean 配置
 *
 * @author crossoverJie
 * Date: 24/05/2018 15:55
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig {

    @Resource
    private AppConfiguration appConfiguration;

    @Resource
    private ShutDownSign shutDownSign;

    @Resource
    private MsgLogger msgLogger;


    @Bean
    public Client buildClient(@Qualifier("callBackThreadPool") ThreadPoolExecutor callbackThreadPool,
                              Event event) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true).build();

        return Client.builder()
                .auth(ClientConfigurationData.Auth.builder()
                        .userName(appConfiguration.getUserName())
                        .userId(appConfiguration.getUserId())
                        .build())
                .routeUrl(appConfiguration.getRouteUrl())
                .loginRetryCount(appConfiguration.getReconnectCount())
                .event(event)
                .reconnectCheck(client -> !shutDownSign.checkStatus())
                .okHttpClient(okHttpClient)
                .messageListener(new MsgCallBackListener(msgLogger, event))
                .callbackThreadPool(callbackThreadPool)
                .backoffStrategy(new RandomBackoff())
                .build();
    }

    /**
     * http client
     *
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }


    /**
     * Create callback thread pool
     *
     * @return
     */
    @Bean("callBackThreadPool")
    public ThreadPoolExecutor buildCallerThread() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(appConfiguration.getQueueSize());
        ThreadFactory executor = new ThreadFactoryBuilder()
                .setNameFormat("msg-callback-%d")
                .setDaemon(true)
                .build();
        return new ThreadPoolExecutor(appConfiguration.getPoolSize(), appConfiguration.getPoolSize(), 1,
                TimeUnit.MILLISECONDS, queue, executor);
    }


    @Bean
    public RingBufferWheel bufferWheel() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        return new RingBufferWheel(executorService);
    }

}
