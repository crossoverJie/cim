package com.crossoverjie.cim.server.decorator;

import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgLastSendRecordService;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.crossoverjie.cim.common.constant.Constants.FETCH_OFFLINE_MSG_LIMIT;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
@Repository("basicDbStore")
public class BasicDbStore implements OfflineMsgStore {

    @Autowired
    private OfflineMsgService offlineMsgService;
    @Autowired
    private OfflineMsgLastSendRecordService offlineMsgLastSendRecordService;

    @Override
    public void save(OfflineMsg offlineMsg) {
        offlineMsgService.save(offlineMsg);
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {
        return offlineMsgService.fetchOfflineMsgsWithCursor(userId, FETCH_OFFLINE_MSG_LIMIT);
    }

    @Override
    public void markDelivered(Long userId, List<Long> messageIds) {
        offlineMsgService.updateStatus(userId, messageIds);
        offlineMsgLastSendRecordService.saveLatestMessageId(userId, messageIds.get(messageIds.size() - 1));
    }
}
