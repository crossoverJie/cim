package com.crossoverjie.cim.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenqwwq
 * @date 2025/6/8
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "cim.server")
public class ServerConfiguration {


    /**
     * 链接服务端注册类型
     * <p>
     * zk: zookeeper 存储
     *
     * @see com.crossoverjie.cim.common.enums.RegistryType 注册类型
     */
    @Value("${register.type:no}")
    private Boolean registerType;

}
