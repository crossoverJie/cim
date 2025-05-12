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
    private final OfflineMsgLockManager lockManager;

    public RedisWalDecorator(@Qualifier("basicDbStore") OfflineStore basicDbStore, RedisWALService wal, OfflineMsgService offlineMsgService, OfflineMsgLockManager offlineMsgLockManager) {
        super(basicDbStore);
        this.wal = wal;
        this.offlineMsgService = offlineMsgService;
        this.lockManager = offlineMsgLockManager;
    }

    //todo restore mechanism? 数据库要是连接异常，那估计短时间内都连接不上？那重试机制还有必要嘛。不如等redis补偿

    //todo 数据不一致： 从redis中fetch后，进行下发，下发过程中，执行了consumeRedis的任务，未下发状态去到mysql，这样会下发两次
    // ---redis中userId加多个处理中的状态，若是处理中，则等候一下
    @Override
    public void save(OfflineMsg offlineMsg) {

        Long userId = offlineMsg.getUserId();
        lockManager.withWriteLock(userId, () -> {
            //todo 这样能提高并发量，但是可能会有数据延迟发送的风险，
            // 存储redis后，执行定时任务前，redis异常，那数据会延迟下发。 redis一直异常期间数据是正常的
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
        });


        //todo 这样能保证数据的一致性，但是并发量上面高
//        boolean walAvailable = true;
//        boolean dbAvailable = true;
//
//        try {
//            wal.saveOfflineMsgToWal(offlineMsg);
//        } catch (Exception e) {
//            walAvailable = false;
//            log.error("save offline msg in the redis error", e);
//        }
//
//        try {
//            super.save(offlineMsg);
//        } catch (Exception e) {
//            dbAvailable = false;
//            log.error("save offline msg in the database error", e);
//        }
//
//        if (!walAvailable && !dbAvailable) {
//            throw new CIMException(StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR);
//        }
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {

        List<OfflineMsg> msgs = new ArrayList<>();
        lockManager.withReadLock(userId, () -> {

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
        });
        return msgs;


//        List<OfflineMsg> msgs = new ArrayList<>();
//
//        //获取redis数据之前，先进行一波下发
//        wal.migrateOfflineMsgToDb(userId);
//
//        List<OfflineMsg> msgsFromRedis = wal.getOfflineMsgs(userId);
//        if (!CollectionUtils.isEmpty(msgsFromRedis)) {
//            msgs.addAll(msgsFromRedis);
//        }
//
//        List<OfflineMsg> msgsFromDb = super.fetch(userId);
//        if (!CollectionUtils.isEmpty(msgsFromDb)) {
//            msgs.addAll(msgsFromDb);
//        }
//        return msgs;
    }

    @Override
    public void markDelivered(Long userId, List<String> messageIds) {
        lockManager.withWriteLock(userId, () -> {
            super.markDelivered(userId, messageIds);
            messageIds.stream().forEach(id -> wal.markDelivered(id));
        });
    }
}

