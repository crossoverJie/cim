package com.crossoverjie.cim.server.scheduleTask;

import com.crossoverjie.cim.server.service.impl.RedisOfflineMsgBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersistOfflineMsgTask {

    @Autowired
    private RedisOfflineMsgBuffer redisOfflineMsgBuffer;


//    @Scheduled(cron = "0/50 * * * * ?")
    public void persistOfflineMsg() {
        log.info("start offline msg buffer consume");
        redisOfflineMsgBuffer.startOfflineMsgsBufferConsume();
    }
}
