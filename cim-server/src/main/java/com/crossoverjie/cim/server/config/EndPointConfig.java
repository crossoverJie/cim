package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.server.endpoint.CustomEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Function: 监控端点配置
 *
 * @author crossoverJie
 *         Date: 17/04/2018 15:48
 * @since JDK 1.8
 */
@Configuration
public class EndPointConfig {


    @Value("${monitor.channel.map.key}")
    private String channelMap;

    @Bean
    public CustomEndpoint buildEndPoint(){
        CustomEndpoint customEndpoint = new CustomEndpoint(channelMap) ;
        return customEndpoint ;
    }
}
