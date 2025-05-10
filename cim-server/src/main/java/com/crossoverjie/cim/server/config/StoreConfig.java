package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.server.decorator.BasicDbStore;
import com.crossoverjie.cim.server.decorator.OfflineStore;
import com.crossoverjie.cim.server.decorator.RedisWalDecorator;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import com.crossoverjie.cim.server.service.RedisWALService;
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
    public OfflineStore primaryStore(OfflineStore basicMysqlStore) {
        return basicMysqlStore;
    }

    @Bean
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = "redis_mysql")
    public OfflineStore redisMysqlStore(
            BasicDbStore dbStore,
            RedisWALService wal,
            OfflineMsgService offlineMsgService) {
        return new RedisWalDecorator(dbStore, wal, offlineMsgService);
    }
}
