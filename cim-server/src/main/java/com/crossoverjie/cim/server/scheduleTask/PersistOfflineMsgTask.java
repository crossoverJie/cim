package com.crossoverjie.cim.server.scheduleTask;

import com.crossoverjie.cim.server.service.RedisWALService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PersistOfflineMsgTask {

    @Autowired
    private RedisWALService redisWALServicel;


//    @Scheduled(cron = "0/20 * * * * ?")
    public void persistOfflineMsg() {
        redisWALServicel.startOfflineMsgsWALConsumer();
    }
}
