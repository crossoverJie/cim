package com.crossoverjie.cim.persistence.redis.impl;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.persistence.api.annotation.RedisLock;
import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgBufferService;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.crossoverjie.cim.common.constant.Constants.OFFLINE_MSG_DELIVERED;
import static com.crossoverjie.cim.common.constant.Constants.OFFLINE_MSG_PENDING;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Service
@Slf4j
public class OfflineMsgBufferServiceImpl implements OfflineMsgBufferService {

    private static final String MSG_KEY = "offline:msg:";
    private static final String USER_IDX = "offline:msg:user:";

    @Autowired
    private RedisTemplate<String, Object> redis;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private OfflineMsgService offlineMsgService;
    @Autowired
    private Jackson2HashMapper hashMapper;
    @Lazy
    @Autowired
    private OfflineMsgBufferServiceImpl selfProxy;


    public void saveOfflineMsgInBuffer(OfflineMsg msg) {
        String key = MSG_KEY + msg.getMessageId();

        Map<String, Object> hashMap = hashMapper.toHash(msg);
        redis.opsForHash().putAll(key, hashMap);
        redis.opsForList().rightPush(USER_IDX + msg.getReceiveUserId(), msg.getMessageId().toString());
    }

    @Override
    public void deleteOfflineMsgFromBuffer(Long messageId) {
        redis.delete(MSG_KEY + messageId);
    }

    @Override
    public void markDelivered(Long messageId) {
        String key = MSG_KEY + messageId;
        if (Boolean.TRUE.equals(redis.hasKey(key))) {
            redis.opsForHash().put(key, "status", OFFLINE_MSG_DELIVERED);
        }
    }

    @Override
    public List<OfflineMsg> getOfflineMsgs(Long userId, boolean includeAcked) {
        String idxKey = USER_IDX + userId;
        List<String> ids = redis.opsForList().range(idxKey, 0, -1)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids))
            return Collections.emptyList();

        List<OfflineMsg> offlineMsgs = new ArrayList<>();
        for (String id : ids) {
            String key = MSG_KEY + id;
            Map<Object, Object> map = redis.opsForHash().entries(key);
            if (!map.isEmpty()) {
                OfflineMsg msg = mapper.convertValue(map, OfflineMsg.class);
                offlineMsgs.add(msg);
            }
        }

        if (!includeAcked) {
            offlineMsgs = offlineMsgs.stream().filter(msg -> OFFLINE_MSG_PENDING.equals(msg.getStatus())).collect(Collectors.toList());
        }
        return offlineMsgs;
    }


    @Override
    @RedisLock(key = "T(java.lang.String).format('lock:offlineMsg:%s', #userId)", waitTime = 5, leaseTime = 30)
    public void migrateOfflineMsgToDb(Long userId) {
        List<OfflineMsg> offlineMsgs = getOfflineMsgs(userId, true);

        if (CollectionUtils.isEmpty(offlineMsgs)) {
            return;
        }

        List<Long> dbIds = offlineMsgService.fetchOfflineMsgIdsWithCursor(userId);
        offlineMsgs.removeIf(msg -> dbIds.contains(msg.getMessageId()));
        if (CollectionUtils.isEmpty(offlineMsgs)) {
            log.info("no offline msg to migrate");
            cleanOfflineMsgs(userId, offlineMsgs);
            return;
        }
        offlineMsgService.insertBatch(offlineMsgs);
        cleanOfflineMsgs(userId, offlineMsgs);
    }

    public void cleanOfflineMsgs(Long userId, List<OfflineMsg> offlineMsgs) {
        offlineMsgs.forEach(msg -> deleteOfflineMsgFromBuffer(msg.getMessageId()));
        redis.delete(USER_IDX + userId);
    }

    @Override
    public void startOfflineMsgsBufferConsume() {
        try {
            Set<String> userKeys = scanUserKeys(redis.getConnectionFactory());

            userKeys.parallelStream().forEach(key -> {
                Long userId = extractUserId(key);
                if (getLockedUserIdsByScan().contains(userId)) {
                    return;
                }

                selfProxy.migrateOfflineMsgToDb(userId);
            });
        } catch (Exception e) {
            log.error("An exception occurred when consuming offline messages in redis", e);
        }

    }

    public Set<Long> getLockedUserIdsByScan() {
        Set<Long> userIds = new HashSet<>();
        ScanOptions opts = ScanOptions.scanOptions()
                .match("lock:offlineMsg:*")
                .count(500)
                .build();
        RedisConnection conn = redis.getConnectionFactory().getConnection();
        try (Cursor<byte[]> cursor = conn.scan(opts)) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next(), StandardCharsets.UTF_8);
                String idStr = key.substring("lock:offlineMsg:".length());
                userIds.add(Long.valueOf(idStr));
            }
        }
        return userIds;
    }

    private Long extractUserId(String key) {
        String numbers = StringUtils.getDigits(key);
        if (StringUtils.isNotEmpty(numbers)) {
            return Long.parseLong(numbers);
        }
        throw new CIMException("get user id from key failed");
    }

    public Set<String> scanUserKeys(RedisConnectionFactory factory) {
        Set<String> userKeys = new HashSet<>();
        try (RedisConnection connection = factory.getConnection()) {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(USER_IDX + "*")
                    .count(100) // 每批扫描100个键
                    .build();

            Cursor<byte[]> cursor = connection.scan(options);
            while (cursor.hasNext()) {
                byte[] keyBytes = cursor.next();
                userKeys.add(new String(keyBytes, StandardCharsets.UTF_8));
            }
        }
        return userKeys;
    }
}
