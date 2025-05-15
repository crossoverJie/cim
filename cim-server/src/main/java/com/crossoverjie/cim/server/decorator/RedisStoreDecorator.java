package com.crossoverjie.cim.server.decorator;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgBufferService;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
@Service
@Slf4j
public class RedisStoreDecorator extends StoreDecorator {


    private final OfflineMsgBufferService buffer;
    private final OfflineMsgService offlineMsgService;

    public RedisStoreDecorator(@Qualifier("basicDbStore") OfflineMsgStore basicDbStore, OfflineMsgBufferService buffer, OfflineMsgService offlineMsgService) {
        super(basicDbStore);
        this.buffer = buffer;
        this.offlineMsgService = offlineMsgService;
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
        List<OfflineMsg> msgsFromBuffer = new ArrayList<>();
        List<OfflineMsg> msgsFromDb = new ArrayList<>();

        try {
            msgsFromBuffer = buffer.getOfflineMsgs(userId);
        } catch (Exception e) {
            log.error("get offline msg in the redis error", e);
        }

        try {
            msgsFromDb = super.fetch(userId);
        } catch (Exception e) {
            log.error("get offline msg in the database error", e);
        }

        msgs.addAll(msgsFromBuffer);
        msgs.addAll(msgsFromDb);
        return msgs;
    }

    @Override
    public void markDelivered(Long userId, List<Long> messageIds) {

        try {
            messageIds.stream().forEach(id -> buffer.markDelivered(id));
        }catch (Exception e){
            log.error("mark offline msg as delivered in the redis error", e);
        }

        try{
            super.markDelivered(userId, messageIds);
        }catch (Exception e){
            log.error("mark offline msg as delivered in the database error", e);
        }

    }
}

