package com.crossoverjie.cim.route.config;

import com.crossoverjie.cim.persistence.api.service.OfflineMsgLastSendRecordService;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgService;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.OfflineMsgDb;
import com.crossoverjie.cim.persistence.redis.OfflineMsgBuffer;
import com.crossoverjie.cim.persistence.redis.impl.OfflineMsgBufferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Configuration
public class OfflineMsgStoreConfig {

    @Bean
    public OfflineMsgDb offlineMsgDbStore(OfflineMsgService offlineMsgService, OfflineMsgLastSendRecordService offlineMsgLastSendRecordService) {
        return new OfflineMsgDb(offlineMsgService, offlineMsgLastSendRecordService);
    }

    @Bean
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = "bufferingDB")
    public OfflineMsgBuffer offlineMsgBufferStore(OfflineMsgDb db, OfflineMsgBufferServiceImpl buffer) {
        return new OfflineMsgBuffer(db, buffer);
    }

    @Bean
    @Primary
    public OfflineMsgStore offlineMsgStore(
            @Autowired(required = false) OfflineMsgBuffer buffer,
            OfflineMsgDb db) {
        return (buffer != null) ? buffer : db;
    }
}
