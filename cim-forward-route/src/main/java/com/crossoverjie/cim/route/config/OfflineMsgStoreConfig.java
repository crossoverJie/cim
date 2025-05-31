package com.crossoverjie.cim.route.config;

import com.crossoverjie.cim.persistence.mysql.offlinemsg.OfflineMsgDb;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper.OfflineMsgLastSendRecordMapper;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper.OfflineMsgMapper;
import com.crossoverjie.cim.persistence.redis.OfflineMsgBuffer;
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
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = "mysql")
    public OfflineMsgDb offlineMsgDbStore(OfflineMsgMapper offlineMsgMapper, OfflineMsgLastSendRecordMapper offlineMsgLastSendRecordMapper) {
        return new OfflineMsgDb(offlineMsgMapper, offlineMsgLastSendRecordMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "offline.store.mode", havingValue = "redis")
    public OfflineMsgBuffer offlineMsgBufferStore() {
        return new OfflineMsgBuffer();
    }
}
