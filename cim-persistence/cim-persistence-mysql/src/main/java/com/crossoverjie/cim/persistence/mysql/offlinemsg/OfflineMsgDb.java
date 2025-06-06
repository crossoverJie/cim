package com.crossoverjie.cim.persistence.mysql.offlinemsg;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper.OfflineMsgLastSendRecordMapper;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper.OfflineMsgMapper;

import java.util.List;

import static com.crossoverjie.cim.common.constant.Constants.FETCH_OFFLINE_MSG_LIMIT;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
public class OfflineMsgDb implements OfflineMsgStore {

    private final OfflineMsgMapper offlineMsgMapper;
    private final OfflineMsgLastSendRecordMapper offlineMsgLastSendRecordMapper;

    public OfflineMsgDb(OfflineMsgMapper offlineMsgMapper, OfflineMsgLastSendRecordMapper offlineMsgLastSendRecordMapper) {
        this.offlineMsgMapper = offlineMsgMapper;
        this.offlineMsgLastSendRecordMapper = offlineMsgLastSendRecordMapper;
    }

    @Override
    public void save(OfflineMsg offlineMsg) {
        offlineMsgMapper.insert(offlineMsg);
    }

    @Override
    public List<OfflineMsg> fetch(Long receiveUserId) {
        return offlineMsgMapper.fetchOfflineMsgsWithCursor(receiveUserId, FETCH_OFFLINE_MSG_LIMIT);
    }

    @Override
    public void markDelivered(Long receiveUserId, List<Long> messageIds) {
        offlineMsgMapper.updateStatus(receiveUserId, messageIds);
        offlineMsgLastSendRecordMapper.saveLatestMessageId(receiveUserId, messageIds.get(messageIds.size() - 1));
    }
}

