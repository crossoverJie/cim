package com.crossoverjie.cim.server.service.impl;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.mapper.OfflineMsgMapper;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import com.crossoverjie.cim.server.util.SnowflakeIdWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author zhongcanyu
 * @date 2025/5/8
 * @description
 */
@Service
public class OfflineMsgServiceImpl implements OfflineMsgService {

    private final OfflineMsgMapper offlineMsgMapper;

    public OfflineMsgServiceImpl(OfflineMsgMapper offlineMsgMapper) {
        this.offlineMsgMapper = offlineMsgMapper;
    }

    @Override
    public void save(OfflineMsg offlineMsg) {

        offlineMsgMapper.insert(offlineMsg);

    }

    @Override
    public List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, String lastMessageId, int limit) {
        List<OfflineMsg> offlineMsgs = offlineMsgMapper.fetchOfflineMsgsWithCursor(userId, lastMessageId, limit);
        return offlineMsgs;
    }
}
