package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.server.decorator.BasicDbStore;
import com.crossoverjie.cim.server.decorator.OfflineMsgStore;
import com.crossoverjie.cim.server.decorator.RedisStoreDecorator;
import com.crossoverjie.cim.server.service.OfflineMsgBufferService;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class StoreConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name="offline.store.mode", havingValue="mysql", matchIfMissing=true)
    public OfflineMsgStore primaryStore(OfflineMsgStore basicMysqlStore) {
        return basicMysqlStore;
    }

    @Bean
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = "redis_mysql")
    @ConditionalOnMissingBean(name = "primaryStore")
    public OfflineMsgStore redisMysqlStore(
            BasicDbStore dbStore,
            OfflineMsgBufferService buffer,
            OfflineMsgService offlineMsgService) {
        return new RedisStoreDecorator(dbStore, buffer, offlineMsgService);
    }
}
