package com.crossoverjie.cim.server.service.impl;

import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.RedisWALService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RedisWALServiceImpl implements RedisWALService {
    @Override
    public void logOfflineMsg(OfflineMsg msg) {

    }

    @Override
    public List<OfflineMsg> getOfflineMsgs(Long userId) {
        return List.of();
    }

    @Override
    public void startWALConsumer() {

    }
}
