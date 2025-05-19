package com.crossoverjie.cim.persistence.mysql.offlinemsg.impl;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgService;
import com.crossoverjie.cim.persistence.api.util.SnowflakeIdWorker;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper.OfflineMsgMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Service
public class OfflineMsgServiceImpl implements OfflineMsgService {

    private final OfflineMsgMapper offlineMsgMapper;
    private final SnowflakeIdWorker idWorker;
    private final ObjectMapper objectMapper;

    public OfflineMsgServiceImpl(OfflineMsgMapper offlineMsgMapper, SnowflakeIdWorker idWorker, ObjectMapper objectMapper) {
        this.offlineMsgMapper = offlineMsgMapper;
        this.idWorker = idWorker;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(OfflineMsg offlineMsg) {

        offlineMsgMapper.insert(offlineMsg);

    }

    @Override
    public List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, int limit) {
        List<OfflineMsg> offlineMsgs = offlineMsgMapper.fetchOfflineMsgsWithCursor(userId, limit);
        return offlineMsgs;
    }

    @Override
    public void updateStatus(Long userId, List<Long> messageIds) {
        offlineMsgMapper.updateStatus(userId, messageIds);
    }

    @Override
    public List<Long> fetchOfflineMsgIdsWithCursor(Long userId) {
        return offlineMsgMapper.fetchOfflineMsgIdsWithCursor(userId);
    }

    @Override
    public Integer insertBatch(List<OfflineMsg> offlineMsgs) {
        return offlineMsgMapper.insertBatch(offlineMsgs);
    }

}
