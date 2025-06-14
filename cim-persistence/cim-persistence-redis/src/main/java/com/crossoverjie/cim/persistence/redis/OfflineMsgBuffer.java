package com.crossoverjie.cim.persistence.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.crossoverjie.cim.persistence.redis.kit.OfflineMsgScriptExecutor;
import lombok.extern.slf4j.Slf4j;
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

    public OfflineMsgBuffer(OfflineMsgScriptExecutor scriptExecutor, Integer configuredDays) {
        this.messageTtlDays = ensureValidTtlOrDefault(configuredDays);
        this.scriptExecutor = scriptExecutor;
    }

    private int ensureValidTtlOrDefault(Integer configuredDays) {
        return (configuredDays != null && configuredDays > 0) ? configuredDays : OFFLINE_MSG_TTL_DAYS;
    }

    @Override
    public void save(OfflineMsg msg) {
        try {
            scriptExecutor.saveOfflineMsg(msg, messageTtlDays);
        } catch (Exception e) {
            throw new CIMException(StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR);
        }
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {
        try {
            List<String> jsonResult = scriptExecutor.fetchOfflineMsgs(userId, FETCH_OFFLINE_MSG_SIZE);
            List<OfflineMsg> offlineMsgs = jsonResult.stream().map(json -> JSON.parseObject(json, new TypeReference<OfflineMsg>() {
            })).collect(Collectors.toList());
            return offlineMsgs;
        } catch (Exception e) {
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
        } catch (Exception e) {
            throw new CIMException(StatusEnum.OFFLINE_MESSAGE_DELETE_ERROR);
        }
    }
}
