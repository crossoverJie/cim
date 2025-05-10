package com.crossoverjie.cim.server.decorator;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import com.crossoverjie.cim.server.service.RedisWALService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
@Service
@Slf4j
public class RedisWalDecorator extends StoreDecorator {


    private final RedisWALService redisWALService;
    private final OfflineMsgService offlineMsgService;

    public RedisWalDecorator(@Qualifier("basicDbStore") OfflineStore delegate, RedisWALService wal, OfflineMsgService offlineMsgService) {
        super(delegate);
        this.redisWALService = wal;
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
            redisWALService.saveOfflineMsgToWal(offlineMsg);
        } catch (Exception e) {
            redisAvailable = false;
            log.error("save offline msg in the redis error", e);
        }

        try {
            offlineMsgService.save(offlineMsg);
            redisWALService.deleteOfflineMsgFromWal(offlineMsg.getMessageId());
        } catch (Exception e) {
            dbAvailable = false;
            log.error("save offline msg in the database error", e);
        }

        if (!(redisAvailable && dbAvailable)) {
            throw new CIMException("save offline msg error");
        }

//        // 先写 WAL
//        wal.saveOfflineMsgToWal(msg);
//        // 再调用下层（可能是 MySQL，也可能是另一个 decorator）
//        super.save(msg);
//        // 如果下层成功，可删 WAL
//        wal.deleteOfflineMsgFromWal(msg.getMessageId());
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {
//        // 先尝试下层（MySQL）
//        List<OfflineMsg> list = super.fetch(userId);
//        if (!list.isEmpty()) return list;
//        // 回落到 WAL
//        return redisWALService.pullOfflineMsgFromWal(userId);
        return null;
    }
}

