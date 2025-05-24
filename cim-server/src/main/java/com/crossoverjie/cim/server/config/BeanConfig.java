package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.common.core.proxy.RpcProxyManager;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.metastore.ZkMetaStoreImpl;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.route.api.RouteApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 00:25
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig {

    @Resource
    private AppConfiguration appConfiguration;

    /**
     * http client
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    @Bean
    public MetaStore metaStore() {
        return new ZkMetaStoreImpl();
    }

    /**
     * 创建心跳单例
     * @return
     */
    @Bean(value = "heartBeat")
    public Request heartBeat() {
        return Request.newBuilder()
                .setRequestId(0L)
                .setReqMsg("pong")
                .setCmd(BaseCommand.PING)
                .build();
    }

    @Bean
    public RouteApi routeApi(OkHttpClient okHttpClient) {
        return RpcProxyManager.create(RouteApi.class, appConfiguration.getRouteUrl(), okHttpClient);
    }
}
