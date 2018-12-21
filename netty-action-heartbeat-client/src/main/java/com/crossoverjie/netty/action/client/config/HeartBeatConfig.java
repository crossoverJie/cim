package com.crossoverjie.netty.action.client.config;

import com.crossoverjie.netty.action.common.protocol.BaseRequestProto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Function:构建心跳使用的 bean
 *
 * @author crossoverJie
 *         Date: 24/05/2018 15:55
 * @since JDK 1.8
 */
@Configuration
public class HeartBeatConfig {

    @Value("${client.request.id}")
    private int requestId;


    @Bean(value = "heartBeat")
    public BaseRequestProto.RequestProtocol heartBeat() {

        BaseRequestProto.RequestProtocol heart = BaseRequestProto.RequestProtocol.newBuilder()
                .setRequestId(requestId)
                .setReqMsg("ping")
                .build();

        return heart;
    }
}
