package com.crossoverjie.cim.persistence.mysql.config;

import com.crossoverjie.cim.persistence.mysql.util.MapToJsonTypeHandler;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Configuration
public class MyBatisConfig {
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry().register(MapToJsonTypeHandler.class);
        };
    }
}
