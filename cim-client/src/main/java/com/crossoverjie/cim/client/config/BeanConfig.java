package com.crossoverjie.cim.client.config;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Function:bean 配置
 *
 * @author crossoverJie
 *         Date: 24/05/2018 15:55
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig {

    @Value("${cim.user.id}")
    private long userId;


    /**
     * 创建心跳单例
     * @return
     */
    @Bean(value = "heartBeat")
    public CIMRequestProto.CIMReqProtocol heartBeat() {
        CIMRequestProto.CIMReqProtocol heart = CIMRequestProto.CIMReqProtocol.newBuilder()
                .setRequestId(userId)
                .setReqMsg("ping")
                .setType(Constants.CommandType.PING)
                .build();
        return heart;
    }


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

}
