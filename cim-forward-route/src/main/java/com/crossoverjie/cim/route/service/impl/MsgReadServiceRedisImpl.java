package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.route.constant.Constant;
import com.crossoverjie.cim.route.service.MsgReadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

/**
 * Function: 消息已读状态服务 Redis 实现
 *
 * @author crossoverJie
 * Date: 2026/05/02
 * @since JDK 1.8
 */
@Slf4j
@Service
public class MsgReadServiceRedisImpl implements MsgReadService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private RedisMessageListenerContainer redisMessageListenerContainer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        redisMessageListenerContainer.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String channel = new String(message.getChannel());
                    String body = new String(message.getBody());
                    log.info("Received message from channel [{}]: {}", channel, body);
                    
                    ReadStatusSyncMessage syncMessage = objectMapper.readValue(body, ReadStatusSyncMessage.class);
                    
                    String key = Constant.MSG_READ_PREFIX + syncMessage.getMsgId();
                    String value = syncMessage.getUserName() + ":" + syncMessage.getTimestamp();
                    redisTemplate.opsForHash().put(key, String.valueOf(syncMessage.getUserId()), value);
                    
                    log.info("Synced read status: msgId={}, userId={}, userName={}", 
                            syncMessage.getMsgId(), syncMessage.getUserId(), syncMessage.getUserName());
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse sync message", e);
                }
            }
        }, new PatternTopic(Constant.MSG_READ_SYNC_CHANNEL));
        
        log.info("MsgReadServiceRedisImpl initialized, listening on channel: {}", Constant.MSG_READ_SYNC_CHANNEL);
    }

    @Override
    public void markAsRead(long msgId, long userId, String userName) {
        String key = Constant.MSG_READ_PREFIX + msgId;
        long timestamp = System.currentTimeMillis();
        String value = userName + ":" + timestamp;
        
        redisTemplate.opsForHash().put(key, String.valueOf(userId), value);
        
        log.info("Marked message as read: msgId={}, userId={}, userName={}, timestamp={}", 
                msgId, userId, userName, timestamp);
        
        syncReadStatusToCluster(msgId, userId, userName, timestamp);
    }

    @Override
    public boolean isRead(long msgId, long userId) {
        String key = Constant.MSG_READ_PREFIX + msgId;
        return redisTemplate.opsForHash().hasKey(key, String.valueOf(userId));
    }

    @Override
    public Map<Long, String> getReadUsers(long msgId) {
        String key = Constant.MSG_READ_PREFIX + msgId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        
        Map<Long, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            long userId = Long.parseLong((String) entry.getKey());
            String value = (String) entry.getValue();
            String userName = value.split(":")[0];
            result.put(userId, userName);
        }
        
        return result;
    }

    @Override
    public long getReadCount(long msgId) {
        String key = Constant.MSG_READ_PREFIX + msgId;
        return redisTemplate.opsForHash().size(key);
    }

    @Override
    public void removeMsgReadStatus(long msgId) {
        String key = Constant.MSG_READ_PREFIX + msgId;
        redisTemplate.delete(key);
        log.info("Removed message read status: msgId={}", msgId);
    }

    @Override
    public void syncReadStatusToCluster(long msgId, long userId, String userName, long timestamp) {
        try {
            ReadStatusSyncMessage syncMessage = ReadStatusSyncMessage.builder()
                    .msgId(msgId)
                    .userId(userId)
                    .userName(userName)
                    .timestamp(timestamp)
                    .build();
            
            String message = objectMapper.writeValueAsString(syncMessage);
            redisTemplate.convertAndSend(Constant.MSG_READ_SYNC_CHANNEL, message);
            
            log.info("Synced read status to cluster: msgId={}, userId={}", msgId, userId);
        } catch (JsonProcessingException e) {
            log.error("Failed to sync read status to cluster", e);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadStatusSyncMessage {
        private long msgId;
        private long userId;
        private String userName;
        private long timestamp;
    }
}
