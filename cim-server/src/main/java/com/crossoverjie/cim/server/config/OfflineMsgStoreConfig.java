package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.server.decorator.OfflineMsgStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
@Configuration
public class OfflineMsgStoreConfig {

    @Bean
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = "mysql")
    public OfflineMsgStore mysqlOfflineMsgStore(@Qualifier("basicDbStore") OfflineMsgStore basicDbStore) {
        return basicDbStore;
    }

    @Bean
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = "redis_mysql", matchIfMissing = true)
    @Primary
    public OfflineMsgStore redisAndMysqlOfflineMsgStore(@Qualifier("bufferingDbStore") OfflineMsgStore bufferingDbStore) {
        return bufferingDbStore;
    }
}
