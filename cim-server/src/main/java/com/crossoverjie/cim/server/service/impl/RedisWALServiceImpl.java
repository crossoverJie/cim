package com.crossoverjie.cim.server.service.impl;

import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import com.crossoverjie.cim.server.service.RedisWALService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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
    private static final String USER_IDX = "offline:msgs:user:";
    private static final String ALL_MSG_IDS = "offline:msg:all_ids";

    @Autowired
    private RedisTemplate<String, Object> redis;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private OfflineMsgService offlineMsgService;


    public void saveOfflineMsgToWal(OfflineMsg msg) {
        String key = MSG_KEY + msg.getId();
        Map<String, Object> map = new ObjectMapper()
                .convertValue(msg, new TypeReference<>() {
                });
        redis.opsForHash().putAll(key, map);

        //用于处理单个user
        redis.opsForList().rightPush(USER_IDX + msg.getUserId(), msg.getId().toString());
        //用于全局下发
        redis.opsForSet().add(ALL_MSG_IDS, msg.getMessageId());
    }

    @Override
    public void deleteOfflineMsgFromWal(String messageId) {
        redis.opsForHash().delete(MSG_KEY + messageId);
    }

    @Override
    public void markDelivered(String messageId) {
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
        if (ids == null) return Collections.emptyList();

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

    public List<OfflineMsg> getAllOfflineMsgs() {

        List<String> allMsgIds = redis.opsForSet().members(ALL_MSG_IDS)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        List<OfflineMsg> offlineMsgs = new ArrayList<>();
        for (String id : allMsgIds) {
            String key = MSG_KEY + id;
            Map<Object, Object> map = redis.opsForHash().entries(key);
            if (!map.isEmpty()) {
                OfflineMsg msg = mapper.convertValue(map, OfflineMsg.class);
                offlineMsgs.add(msg);
            }
        }
        return offlineMsgs;
    }

    @Override
    public void startOfflineMsgsWALConsumer() {

    }


    @Override
    public void migrateOfflineMsgToDb(Long userId) {
        List<OfflineMsg> offlineMsgs = new ArrayList<>();
        if (userId == null) {
            offlineMsgs = getAllOfflineMsgs();
        } else {
            offlineMsgs = getOfflineMsgs(userId);
        }


        List<String> dbIds = offlineMsgService.fetchOfflineMsgIdsWithCursor(userId);
        offlineMsgs.removeIf(msg -> dbIds.contains(msg.getMessageId()));
        if (CollectionUtils.isEmpty(offlineMsgs)) {
            log.info("no offline msg to migrate");
            return;
        }

        try {
            offlineMsgService.insertBatch(offlineMsgs);
        } catch (Exception e) {
            log.error("migrate offline msg to db failed", e);
            offlineMsgs.stream().forEach(msg -> markDelivered(msg.getMessageId()));
            return;
        }

        offlineMsgs.stream().forEach(msg -> deleteOfflineMsgFromWal(msg.getMessageId()));
        if (userId == null) {
            redis.delete(ALL_MSG_IDS);
        } else {
            redis.delete(USER_IDX + userId);
        }

    }
}
