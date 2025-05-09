package com.crossoverjie.cim.server.service.impl;

import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.RedisWALService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class RedisWALServiceImpl implements RedisWALService {

    @Resource
    private RedisTemplate<String, OfflineMsg> redisTemplate;

    private static final String WAL_KEY_PREFIX = "WAL:OfflineMsg:";


    public void saveOfflineMsgToWal(OfflineMsg msg) {
        String key = WAL_KEY_PREFIX + msg.getMessageId();
        redisTemplate.opsForValue().set(key, msg, Duration.ofMinutes(30));
    }

    public void deleteOfflineMsgFromWal(String messageId) {
        String key = WAL_KEY_PREFIX + messageId;
        redisTemplate.delete(key);
    }

    @Override
    public List<OfflineMsg> getOfflineMsgs(Long userId) {
        return List.of();
    }

    @Override
    public void startWALConsumer() {

    }
}
