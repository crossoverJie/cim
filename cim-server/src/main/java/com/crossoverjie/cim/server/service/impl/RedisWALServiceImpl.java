package com.crossoverjie.cim.server.service.impl;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import com.crossoverjie.cim.server.service.RedisWALService;
import com.crossoverjie.cim.server.util.OfflineMsgLockManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@Slf4j
public class RedisWALServiceImpl implements RedisWALService {

    private static final String MSG_KEY = "offline:msg:";
    private static final String USER_IDX = "offline:msg:user:";
//    private static final String ALL_MSG_IDS = "offline:msg:all_ids";

    @Autowired
    private RedisTemplate<String, Object> redis;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private OfflineMsgService offlineMsgService;
    @Autowired
    private OfflineMsgLockManager lockManager;
    @Autowired
    private Jackson2HashMapper hashMapper;


    public void saveOfflineMsgToRedis(OfflineMsg msg) {
        String key = MSG_KEY + msg.getMessageId();
//        Map<String, Object> map = new HashMap<>();
//        map.put("messageId", msg.getMessageId());
//        map.put("userId", msg.getUserId());
//       // todo msg这里存什么好
//        map.put("content", new String(msg.getContent(), StandardCharsets.UTF_8));
//        map.put("messageType", msg.getMessageType());
//        map.put("status", msg.getStatus());
//        map.put("createdAt", msg.getCreatedAt().toString()); // 或使用DateTimeFormatter
//        map.put("properties", msg.getProperties());

//        redis.opsForHash().putAll(key, map);

        Map<String, Object> hashMap = hashMapper.toHash(msg);
        redis.opsForHash().putAll(key, hashMap);
        //用于处理单个user
        redis.opsForList().rightPush(USER_IDX + msg.getUserId(), msg.getMessageId().toString());
    }

    @Override
    public void deleteOfflineMsgFromRedis(Long messageId) {
        redis.opsForHash().delete(MSG_KEY + messageId);
    }

    @Override
    public void markDelivered(Long messageId) {
        String key = MSG_KEY + messageId;
        redis.opsForHash().put(key, "status", OFFLINE_MSG_DELIVERED);
    }

    @Override
    public List<OfflineMsg> getOfflineMsgs(Long userId) {
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
        return offlineMsgs;
    }

//    public List<OfflineMsg> getAllOfflineMsgs() {
//
//        List<String> allMsgIds = redis.opsForSet().members(ALL_MSG_IDS)
//                .stream()
//                .map(Object::toString)
//                .collect(Collectors.toList());
//
//        List<OfflineMsg> offlineMsgs = new ArrayList<>();
//        for (String id : allMsgIds) {
//            String key = MSG_KEY + id;
//            Map<Object, Object> map = redis.opsForHash().entries(key);
//            if (!map.isEmpty()) {
//                OfflineMsg msg = mapper.convertValue(map, OfflineMsg.class);
//                offlineMsgs.add(msg);
//            }
//        }
//        return offlineMsgs;
//    }

    @Override
    public void migrateOfflineMsgToDb(Long userId) {
        List<OfflineMsg> offlineMsgs = getOfflineMsgs(userId);

        if (CollectionUtils.isEmpty(offlineMsgs)) {
            return;
        }

        List<Long> dbIds = offlineMsgService.fetchOfflineMsgIdsWithCursor(userId);
        offlineMsgs.removeIf(msg -> dbIds.contains(msg.getMessageId()));
        if (CollectionUtils.isEmpty(offlineMsgs)) {
            log.info("no offline msg to migrate");
            clearOfflineMsg(userId, offlineMsgs);
            return;
        }
        offlineMsgService.insertBatch(offlineMsgs);
        clearOfflineMsg(userId, offlineMsgs);
    }

    //todo 获取userId的所有离线消息后，删除前，又有新消息进来了，就会误删
    public void clearOfflineMsg(Long userId, List<OfflineMsg> offlineMsgs) {
        offlineMsgs.stream().forEach(msg -> deleteOfflineMsgFromRedis(msg.getMessageId()));
        redis.delete(USER_IDX + userId);
    }

    @Override
    public void startOfflineMsgsWALConsumer() {
        try {
            Set<String> userKeys = scanUserKeys(redis.getConnectionFactory());

            userKeys.parallelStream().forEach(key -> {
                Long userId = extractUserId(key); // 从 "USER_IDX_1001" 提取 1001
                if (getLockedUserIdsByScan().contains(userId)) {
                    return;
                }

                migrateOfflineMsgToDb(userId);
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
