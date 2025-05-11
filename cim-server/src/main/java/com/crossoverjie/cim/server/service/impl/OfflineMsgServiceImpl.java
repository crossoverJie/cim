package com.crossoverjie.cim.server.service.impl;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.exception.CIMException;
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
    private final SnowflakeIdWorker idWorker;
    private final ObjectMapper objectMapper;

    public OfflineMsgServiceImpl(OfflineMsgMapper offlineMsgMapper, SnowflakeIdWorker idWorker, ObjectMapper objectMapper) {
        this.offlineMsgMapper = offlineMsgMapper;
        this.idWorker = idWorker;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(OfflineMsg offlineMsg) {

        offlineMsgMapper.insert(offlineMsg);

    }

    @Override
    public List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, int limit) {
        List<OfflineMsg> offlineMsgs = offlineMsgMapper.fetchOfflineMsgsWithCursor(userId, limit);
        return offlineMsgs;
    }

    public OfflineMsg createFromVo(SendMsgReqVO vo) {

        try {
            String msgId = String.valueOf(idWorker.nextId());
            byte[] content = objectMapper.writeValueAsBytes(vo);
            return OfflineMsg.builder()
                    .messageId(msgId)
                    .userId(vo.getUserId())
                    .content(content)
                    //todo 写在constants中，而不是直接一个0
                    .messageType(0)
                    .status(0)
                    .createdAt(LocalDateTime.now())
                    .properties(Map.of(
                            Constants.MetaKey.SEND_USER_ID,
                            vo.getProperties().get(Constants.MetaKey.SEND_USER_ID)
                    ))
                    .build();
        } catch (Exception e) {
            throw new CIMException("Failed to create OfflineMsg from SendMsgReqVO", e);
        }
    }

    @Override
    public void updateStatus(Long userId, List<String> messageIds) {
        offlineMsgMapper.updateStatus(userId, messageIds);
    }

    @Override
    public List<String> fetchOfflineMsgIdsWithCursor(Long userId) {
        return offlineMsgMapper.fetchOfflineMsgIdsWithCursor(userId);
    }

    @Override
    public int insertBatch(List<OfflineMsg> offlineMsgs) {
        return offlineMsgMapper.insertBatch(offlineMsgs);
    }

}
