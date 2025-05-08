package com.crossoverjie.cim.msg.storage.service;

import com.crossoverjie.cim.msg.storage.pojo.OfflineMsg;
import com.crossoverjie.cim.msg.storage.util.SnowflakeIdWorker;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/8
 * @description
 */
@Service
public class OfflineMsgServiceImpl implements OfflineMsgService {

    private final SnowflakeIdWorker idWorker;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OfflineMsgServiceImpl(SnowflakeIdWorker idWorker, ObjectMapper objectMapper) {
        this.idWorker = idWorker;
    }

    @Override
    public void save(P2PReqVO vo) {
        String msgId = String.valueOf(idWorker.nextId());
        byte[] content;
        try {
            content = objectMapper.writeValueAsBytes(vo);
        } catch (Exception e) {
            throw new RuntimeException("消息序列化失败", e);
        }

        new OfflineMsg()
                .setMessageId(msgId)
                .setUserId(vo.getReceiveUserId())
                .setContent(content)
                .setMessageType(0)
                .setStatus(0)
                .setCreatedAt(LocalDateTime.now());

        //vo.setProperties(Map.of(
        //                    Constants.MetaKey.USER_ID, String.valueOf(sendUserId),
        //                    Constants.MetaKey.USER_NAME, userInfo.getUserName())
        //            );
    }

    @Override
    public List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, String lastMessageId, int limit) {
        return List.of();
    }
}
