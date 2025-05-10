package com.crossoverjie.cim.server.factory;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.util.SnowflakeIdWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;


@Service
public class OfflineMsgFactory {

    private final SnowflakeIdWorker idWorker;
    private final ObjectMapper objectMapper;

    public OfflineMsgFactory(SnowflakeIdWorker idWorker, ObjectMapper objectMapper) {
        this.idWorker = idWorker;
        this.objectMapper = objectMapper;
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

}
