package com.crossoverjie.cim.route.scheduleTask;

import com.crossoverjie.cim.persistence.api.service.OfflineMsgBufferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersistOfflineMsgTask {

    @Autowired
    private OfflineMsgBufferService offlineMsgBufferService;


    @Scheduled(cron = "0 0/1 * * * ?")
    public void persistOfflineMsg() {
        log.info("start offline msg buffer consume");
        offlineMsgBufferService.startOfflineMsgsBufferConsume();
    }
}
