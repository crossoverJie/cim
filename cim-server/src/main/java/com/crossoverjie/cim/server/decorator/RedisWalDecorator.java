package com.crossoverjie.cim.server.decorator;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import com.crossoverjie.cim.server.service.RedisWALService;
import com.crossoverjie.cim.server.util.OfflineMsgLockManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
@Service
@Slf4j
public class RedisWalDecorator extends StoreDecorator {


    private final RedisWALService wal;
    private final OfflineMsgService offlineMsgService;

    public RedisWalDecorator(@Qualifier("basicDbStore") OfflineStore basicDbStore, RedisWALService wal, OfflineMsgService offlineMsgService) {
        super(basicDbStore);
        this.wal = wal;
        this.offlineMsgService = offlineMsgService;
    }

    //todo restore mechanism? 数据库要是连接异常，那估计短时间内都连接不上？那重试机制还有必要嘛。不如等redis补偿

    @Override
    public void save(OfflineMsg offlineMsg) {

        Long userId = offlineMsg.getUserId();
        //todo 延迟下发风险：存储redis后，执行定时任务前，redis异常，那数据会延迟下发。
        boolean walAvailable = true;
        try {
            wal.saveOfflineMsgToRedis(offlineMsg);
        } catch (Exception e) {
            walAvailable = false;
            log.error("save offline msg in the redis error", e);
        }

        if (!walAvailable) {
            try {
                super.save(offlineMsg);
            } catch (Exception e) {
                log.error("save offline msg in the database error", e);
                throw new CIMException(StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR);
            }

        }
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {

        List<OfflineMsg> msgs = new ArrayList<>();
        List<OfflineMsg> msgsFromRedis = new ArrayList<>();
        List<OfflineMsg> msgsFromDb = new ArrayList<>();

        try {
            msgsFromRedis = wal.getOfflineMsgs(userId);
        } catch (Exception e) {
            log.error("get offline msg in the redis error", e);
        }

        try {
            msgsFromDb = super.fetch(userId);
        } catch (Exception e) {
            log.error("get offline msg in the database error", e);
        }

        msgs.addAll(msgsFromRedis);
        msgs.addAll(msgsFromDb);
        return msgs;
    }

    @Override
    public void markDelivered(Long userId, List<String> messageIds) {
        super.markDelivered(userId, messageIds);
        messageIds.stream().forEach(id -> wal.markDelivered(id));
    }
}

