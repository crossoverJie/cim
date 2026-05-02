package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.route.constant.Constant;
import com.crossoverjie.cim.route.service.MsgMetadataService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Function: 消息元数据服务 Redis 实现
 *
 * @author crossoverJie
 * Date: 2026/05/02
 * @since JDK 1.8
 */
@Slf4j
@Service
public class MsgMetadataServiceRedisImpl implements MsgMetadataService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String MSG_METADATA_PREFIX = "cim-msg-meta:";
    private static final long MSG_METADATA_EXPIRE_DAYS = 7;

    @Override
    public void saveMsgSender(long msgId, long sendUserId) {
        String key = MSG_METADATA_PREFIX + msgId;
        redisTemplate.opsForValue().set(key, String.valueOf(sendUserId));
        redisTemplate.expire(key, MSG_METADATA_EXPIRE_DAYS, TimeUnit.DAYS);
        log.info("Saved message metadata: msgId={}, sendUserId={}", msgId, sendUserId);
    }

    @Override
    public Long getMsgSender(long msgId) {
        String key = MSG_METADATA_PREFIX + msgId;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.error("Failed to parse sendUserId for msgId={}", msgId, e);
            return null;
        }
    }

    @Override
    public void removeMsgMetadata(long msgId) {
        String key = MSG_METADATA_PREFIX + msgId;
        redisTemplate.delete(key);
        log.info("Removed message metadata: msgId={}", msgId);
    }
}
