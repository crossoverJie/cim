package com.crossoverjie.cim.persistence.redis;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgBufferService;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.OfflineMsgDb;
import com.crossoverjie.cim.persistence.redis.impl.OfflineMsgBufferServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Slf4j
//@Service
public class OfflineMsgBuffer implements OfflineMsgStore {

    private final OfflineMsgStore db;
    private final OfflineMsgBufferService buffer;

    public OfflineMsgBuffer(OfflineMsgDb offlineMsgDb, OfflineMsgBufferServiceImpl buffer) {
        this.db = offlineMsgDb;
        this.buffer = buffer;
    }

    //todo restore mechanism? 数据库要是连接异常，那估计短时间内都连接不上？那重试机制还有必要嘛。不如等redis补偿

    @Override
    public void save(OfflineMsg offlineMsg) {
        //todo 延迟下发风险：存储redis后，执行定时任务（将buffer的数据传到db）前，redis异常，那数据会延迟下发（等redis恢复再下发）。
        boolean bufferAvailable = true;
        try {
            buffer.saveOfflineMsgInBuffer(offlineMsg);
        } catch (Exception e) {
            bufferAvailable = false;
            log.error("save offline msg in the buffer error", e);
        }

        if (!bufferAvailable) {
            try {
                db.save(offlineMsg);
            } catch (Exception e) {
                log.error("save offline msg in the database error", e);
                throw new CIMException(StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR);
            }

        }
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {
        boolean bufferAvailable = true;
        boolean dbAvailable = true;

        List<OfflineMsg> msgs = new ArrayList<>();
        List<OfflineMsg> msgsFromBuffer = new ArrayList<>();
        List<OfflineMsg> msgsFromDb = new ArrayList<>();

        try {
            msgsFromBuffer = buffer.getOfflineMsgs(userId, false);
        } catch (Exception e) {
            log.error("get offline msg in the redis error", e);
            bufferAvailable = false;
        }

        try {
            msgsFromDb = db.fetch(userId);
        } catch (Exception e) {
            log.error("get offline msg in the database error", e);
            dbAvailable = false;
        }

        if (!bufferAvailable && !dbAvailable) {
            throw new CIMException(StatusEnum.OFFLINE_MESSAGE_RETRIEVAL_ERROR);
        }

        msgs.addAll(msgsFromBuffer);
        msgs.addAll(msgsFromDb);
        return msgs;
    }

    @Override
    public void markDelivered(Long userId, List<Long> messageIds) {
        boolean bufferAvailable = true;
        boolean dbAvailable = true;

        try {
            messageIds.stream().forEach(id -> buffer.markDelivered(id));
        } catch (Exception e) {
            log.error("mark offline msg as delivered in the redis error", e);
            bufferAvailable = false;
        }

        try {
            db.markDelivered(userId, messageIds);
        } catch (Exception e) {
            log.error("mark offline msg as delivered in the database error", e);
            dbAvailable = false;
        }

        if (!bufferAvailable && !dbAvailable) {
            throw new CIMException(StatusEnum.OFFLINE_MESSAGE_DELIVERY_ERROR);
        }
    }
}
