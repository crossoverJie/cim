package com.crossoverjie.cim.server.service;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.server.mapper.OfflineMsgMapper;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
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

    private final SnowflakeIdWorker idWorker;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OfflineMsgMapper offlineMsgMapper;

    public OfflineMsgServiceImpl(SnowflakeIdWorker idWorker, OfflineMsgMapper offlineMsgMapper) {
        this.idWorker = idWorker;
        this.offlineMsgMapper = offlineMsgMapper;
    }

    @Override
    public void save(P2PReqVO vo) {
        String msgId = String.valueOf(idWorker.nextId());
        byte[] content;
        try {
            content = objectMapper.writeValueAsBytes(vo);
        } catch (Exception e) {
            throw new RuntimeException("Message serialization failed", e);
        }

        OfflineMsg offlineMsg = OfflineMsg.builder()
                .messageId(msgId)
                .userId(vo.getReceiveUserId())
                .content(content)
                .messageType(0)
                .status(0)
                .createdAt(LocalDateTime.now())
                .properties(
                        Map.of(
                                Constants.MetaKey.SEND_USER_ID, String.valueOf(vo.getUserId())
                        )).build();

        offlineMsgMapper.insert(offlineMsg);

    }

    @Override
    public List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, String lastMessageId, int limit) {

        List<OfflineMsg> offlineMsgs = offlineMsgMapper.fetchOfflineMsgsWithCursor(userId, lastMessageId, limit);
        return offlineMsgs;
    }
}
