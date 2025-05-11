package com.crossoverjie.cim.server.decorator;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import com.crossoverjie.cim.server.service.RedisWALService;
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

    //todo These steps are completed asynchronously
    //todo batch storage?
    //todo restore mechanism? 数据库要是连接异常，那估计短时间内都连接不上？那重试机制还有必要嘛。不如等redis补偿
    @Override
    public void save(OfflineMsg offlineMsg) {

        boolean redisAvailable = true;
        boolean dbAvailable = true;

        try {
            wal.saveOfflineMsgToWal(offlineMsg);
        } catch (Exception e) {
            redisAvailable = false;
            log.error("save offline msg in the redis error", e);
        }

        try {
            super.save(offlineMsg);
        } catch (Exception e) {
            dbAvailable = false;
            log.error("save offline msg in the database error", e);
        }

        if (!(redisAvailable && dbAvailable)) {
            throw new CIMException("save offline msg error");
        }
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {

        List<OfflineMsg> msgs = new ArrayList<>();

        List<OfflineMsg> msgsFromDb = super.fetch(userId);
        if (!CollectionUtils.isEmpty(msgsFromDb)) {
            msgs.addAll(msgsFromDb);
        }

        //获取redis数据之前，先进行一波下发
        wal.migrateOfflineMsgToDb(userId);

        List<OfflineMsg> msgsFromRedis = wal.getOfflineMsgs(userId);
        if (!CollectionUtils.isEmpty(msgsFromRedis)) {
            msgs.addAll(msgsFromRedis);
        }
        return msgs;
    }

    @Override
    public void markDelivered(Long userId, List<String> messageIds) {
        super.markDelivered(userId, messageIds);
        messageIds.stream().forEach(id -> wal.markDelivered(id));
    }
}

