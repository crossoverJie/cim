package com.crossoverjie.cim.server.service.impl;

import com.crossoverjie.cim.server.mapper.OfflineMsgLastSendRecordMapper;
import com.crossoverjie.cim.server.service.OfflineMsgLastSendRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
@Service
public class OfflineMsgLastSendRecordServiceImpl implements OfflineMsgLastSendRecordService {

    @Autowired
    private OfflineMsgLastSendRecordMapper offlineMsgLastSendRecordMapper;

    @Override
    public void saveLatestMessageId(Long userId, String lastMessageId) {

    }

    @Override
    public String getLatestMessageId(Long userId) {
        return "";
    }
}
