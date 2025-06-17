package com.crossoverjie.cim.persistence.redis;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.crossoverjie.cim.persistence.redis.kit.OfflineMsgScriptExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.crossoverjie.cim.persistence.redis.constant.Constant.*;


/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Slf4j
public class OfflineMsgBuffer implements OfflineMsgStore {

    private final int messageTtlDays;
    private final OfflineMsgScriptExecutor scriptExecutor;
    private final ObjectMapper objectMapper;

    public OfflineMsgBuffer(OfflineMsgScriptExecutor scriptExecutor, Integer configuredDays,ObjectMapper objectMapper) {
        this.messageTtlDays = ensureValidTtlOrDefault(configuredDays);
        this.scriptExecutor = scriptExecutor;
        this.objectMapper = objectMapper;
    }

    private int ensureValidTtlOrDefault(Integer configuredDays) {
        return (configuredDays != null && configuredDays > 0) ? configuredDays : OFFLINE_MSG_TTL_DAYS;
    }

    @Override
    public void save(OfflineMsg msg) {
        try {
            scriptExecutor.saveOfflineMsg(msg, messageTtlDays);
        } catch (SerializationException | RedisException e) {
            log.error("Failed to save offline message", e);
            throw new CIMException(StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR);
        }
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {
        try {
            List<String> jsonResult = scriptExecutor.fetchOfflineMsgs(userId, FETCH_OFFLINE_MSG_SIZE);
            List<OfflineMsg> offlineMsgs = jsonResult.stream()
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, OfflineMsg.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to parse OfflineMsg JSON", e);
                        }
                    })
                    .collect(Collectors.toList());
            return offlineMsgs;
        } catch (SerializationException | RedisException e) {
            log.error("Failed to fetch offline messages for userId: {}", userId, e);
            throw new CIMException(StatusEnum.OFFLINE_MESSAGE_FETCH_ERROR);
        }
    }

    @Override
    public void markDelivered(Long userId, List<Long> messageIds) {
        if (CollectionUtils.isEmpty(messageIds)) {
            return;
        }
        try {
            scriptExecutor.deleteOfflineMsg(userId, messageIds);
        } catch (RedisException e) {
            log.error("Failed to delete offline messages for userId: {}", userId, e);
            throw new CIMException(StatusEnum.OFFLINE_MESSAGE_DELETE_ERROR);
        }
    }
}
