package com.crossoverjie.cim.server.factory;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.server.api.vo.req.SaveOfflineMsgReqVO;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.util.SnowflakeIdWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static com.crossoverjie.cim.common.constant.Constants.*;


@Service
public class OfflineMsgFactory {

    private final SnowflakeIdWorker idWorker;
    private final ObjectMapper objectMapper;

    public OfflineMsgFactory(SnowflakeIdWorker idWorker, ObjectMapper objectMapper) {
        this.idWorker = idWorker;
        this.objectMapper = objectMapper;
    }

    public OfflineMsg createFromVo(SaveOfflineMsgReqVO vo) {

        try {
            Long msgId = idWorker.nextId();
            return OfflineMsg.builder()
                    .messageId(msgId)
                    .userId(vo.getUserId())
                    .content(vo.getMsg())
                    .messageType(MSG_TYPE_TEXT)
                    .status(OFFLINE_MSG_PENDING)
                    .createdAt(LocalDateTime.now())
                    .properties(Map.of(
                            Constants.MetaKey.USER_NAME,
                            vo.getProperties().get(Constants.MetaKey.USER_NAME)
                    ))
                    .build();
        } catch (Exception e) {
            throw new CIMException("Failed to create OfflineMsg from SendMsgReqVO", e);
        }
    }

}
