package com.crossoverjie.cim.route.config;

import com.crossoverjie.cim.persistence.mysql.offlinemsg.OfflineMsgDb;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper.OfflineMsgLastSendRecordMapper;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper.OfflineMsgMapper;
import com.crossoverjie.cim.persistence.redis.OfflineMsgBuffer;
import com.crossoverjie.cim.persistence.redis.kit.OfflineMsgScriptExecutor;
import com.crossoverjie.cim.route.constant.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Configuration
public class OfflineMsgStoreConfig {

    @Bean
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = Constant.OfflineStoreMode.MYSQL)
    public OfflineMsgDb offlineMsgDbStore(OfflineMsgMapper offlineMsgMapper, OfflineMsgLastSendRecordMapper offlineMsgLastSendRecordMapper) {
        return new OfflineMsgDb(offlineMsgMapper, offlineMsgLastSendRecordMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = Constant.OfflineStoreMode.REDIS)
    public OfflineMsgBuffer offlineMsgBufferStore(OfflineMsgScriptExecutor scriptExecutor, @Value("${offline.store.redis.expire.message-ttl-days}") Integer configuredDays, ObjectMapper objectMapper) {
        return new OfflineMsgBuffer(scriptExecutor, configuredDays, objectMapper);
    }
}

