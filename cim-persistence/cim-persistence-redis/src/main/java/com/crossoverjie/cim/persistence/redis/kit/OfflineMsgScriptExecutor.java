package com.crossoverjie.cim.persistence.redis.kit;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.crossoverjie.cim.persistence.redis.constant.Constant.MSG_KEY;
import static com.crossoverjie.cim.persistence.redis.constant.Constant.USER_IDX;

/**
 * @author zhongcanyu
 * @date 2025/6/13
 * @description
 */
@Component
public class OfflineMsgScriptExecutor {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public OfflineMsgScriptExecutor(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private static final DefaultRedisScript<Long> SAVE_OFFLINE_MSG_SCRIPT;
    private static final DefaultRedisScript<List> FETCH_OFFLINE_MSG_SCRIPT;
    private static final DefaultRedisScript<Long> DELETE_OFFLINE_MSG_SCRIPT;

    static {
        SAVE_OFFLINE_MSG_SCRIPT = new DefaultRedisScript<>();
        SAVE_OFFLINE_MSG_SCRIPT.setLocation(new ClassPathResource("lua/saveOfflineMsg.lua"));
        SAVE_OFFLINE_MSG_SCRIPT.setResultType(Long.class);

        FETCH_OFFLINE_MSG_SCRIPT = new DefaultRedisScript<>();
        FETCH_OFFLINE_MSG_SCRIPT.setLocation(new ClassPathResource("lua/fetchOfflineMsg.lua"));
        FETCH_OFFLINE_MSG_SCRIPT.setResultType(List.class);

        DELETE_OFFLINE_MSG_SCRIPT = new DefaultRedisScript<>();
        DELETE_OFFLINE_MSG_SCRIPT.setLocation(new ClassPathResource("lua/deleteOfflineMsg.lua"));
        DELETE_OFFLINE_MSG_SCRIPT.setResultType(Long.class);
    }

    public Long saveOfflineMsg(OfflineMsg msg, Integer messageTtlDays) {
        List<String> keys = Arrays.asList(MSG_KEY, USER_IDX);
        List<Object> allArgs = new ArrayList<>();
        allArgs.add(msg.getMessageId());
        allArgs.add(msg.getReceiveUserId());
        allArgs.add(serialize(msg));
        allArgs.add(Duration.ofDays(messageTtlDays).getSeconds());

        return redisTemplate.execute(SAVE_OFFLINE_MSG_SCRIPT, keys, allArgs.toArray());
    }

    private String serialize(OfflineMsg msg) {
        try {
            return objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            throw new SerializationException("OfflineMsg serialization failed", e);
        }
    }


    public List<String> fetchOfflineMsgs(Long userId, Integer size) {
        List<String> keys = Arrays.asList(MSG_KEY, USER_IDX);
        List<Object> allArgs = new ArrayList<>();
        allArgs.add(userId);
        allArgs.add(size);

        return (List<String>) redisTemplate.execute(FETCH_OFFLINE_MSG_SCRIPT, keys, allArgs.toArray());
    }

    public Long deleteOfflineMsg(Long userId, List<Long> msgIds) {
        List<String> keys = Arrays.asList(MSG_KEY, USER_IDX);
        List<Object> allArgs = new ArrayList<>();
        allArgs.add(userId);
        allArgs.addAll(msgIds);

        return redisTemplate.execute(DELETE_OFFLINE_MSG_SCRIPT, keys, allArgs.toArray());
    }
}
