package com.crossoverjie.cim.persistence.redis;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.crossoverjie.cim.common.constant.Constants.OFFLINE_MSG_DELIVERED;
import static com.crossoverjie.cim.common.constant.Constants.OFFLINE_MSG_PENDING;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Slf4j
public class OfflineMsgBuffer implements OfflineMsgStore {

    private static final String MSG_KEY = "offline:msg:";
    private static final String USER_IDX = "offline:msg:user:";

    @Autowired
    private RedisTemplate<String, Object> redis;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private Jackson2HashMapper hashMapper;

    @Override
    public void save(OfflineMsg msg) {
        String key = MSG_KEY + msg.getMessageId();

        Map<String, Object> hashMap = hashMapper.toHash(msg);
        redis.opsForHash().putAll(key, hashMap);
        redis.opsForList().rightPush(USER_IDX + msg.getReceiveUserId(), msg.getMessageId().toString());
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {
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
        offlineMsgs = offlineMsgs.stream().filter(msg -> OFFLINE_MSG_PENDING.equals(msg.getStatus())).collect(Collectors.toList());
        return offlineMsgs;
    }

    @Override
    public void markDelivered(Long userId, List<Long> messageIds) {
        messageIds.stream().forEach(messageId -> {
            String key = MSG_KEY + messageId;
            if (Boolean.TRUE.equals(redis.hasKey(key))) {
                redis.opsForHash().put(key, "status", OFFLINE_MSG_DELIVERED);
            }
        });
    }
}
