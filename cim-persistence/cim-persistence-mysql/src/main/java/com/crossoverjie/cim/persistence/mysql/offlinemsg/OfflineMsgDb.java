package com.crossoverjie.cim.persistence.mysql.offlinemsg;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgLastSendRecordService;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgService;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.impl.OfflineMsgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.crossoverjie.cim.common.constant.Constants.FETCH_OFFLINE_MSG_LIMIT;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
//@Service
public class OfflineMsgDb implements OfflineMsgStore {

    private final OfflineMsgService offlineMsgService;
    private final OfflineMsgLastSendRecordService offlineMsgLastSendRecordService;

    public OfflineMsgDb(OfflineMsgService offlineMsgService, OfflineMsgLastSendRecordService offlineMsgLastSendRecordService) {
        this.offlineMsgService = offlineMsgService;
        this.offlineMsgLastSendRecordService = offlineMsgLastSendRecordService;
    }

    @Override
    public void save(OfflineMsg offlineMsg) {
        offlineMsgService.save(offlineMsg);
    }

    @Override
    public List<OfflineMsg> fetch(Long receiveUserId) {
        return offlineMsgService.fetchOfflineMsgsWithCursor(receiveUserId, FETCH_OFFLINE_MSG_LIMIT);
    }

    @Override
    public void markDelivered(Long receiveUserId, List<Long> messageIds) {
        offlineMsgService.updateStatus(receiveUserId, messageIds);
        offlineMsgLastSendRecordService.saveLatestMessageId(receiveUserId, messageIds.get(messageIds.size() - 1));
    }
}

