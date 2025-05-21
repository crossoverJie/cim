package com.crossoverjie.cim.persistence.mysql.offlinemsg.impl;

import com.crossoverjie.cim.persistence.api.service.OfflineMsgLastSendRecordService;
import com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper.OfflineMsgLastSendRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Service
public class OfflineMsgLastSendRecordServiceImpl implements OfflineMsgLastSendRecordService {

    @Autowired
    private OfflineMsgLastSendRecordMapper offlineMsgLastSendRecordMapper;

    @Override
    public void saveLatestMessageId(Long receiveUserId, Long lastMessageId) {
        offlineMsgLastSendRecordMapper.saveLatestMessageId(receiveUserId,lastMessageId);
    }

    @Override
    public String getLatestMessageId(Long receiveUserId) {
        return offlineMsgLastSendRecordMapper.getLatestMessageId(receiveUserId);
    }
}

