package com.crossoverjie.cim.route.factory;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.util.SnowflakeIdWorker;
import com.crossoverjie.cim.persistence.api.vo.req.SaveOfflineMsgReqVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import static com.crossoverjie.cim.common.constant.Constants.MSG_TYPE_TEXT;
import static com.crossoverjie.cim.common.constant.Constants.OFFLINE_MSG_PENDING;

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
                    .receiveUserId(vo.getReceive_user_id())
                    .content(vo.getMsg())
                    .messageType(MSG_TYPE_TEXT)
                    .status(OFFLINE_MSG_PENDING)
                    .createdAt(LocalDateTime.now())
                    .properties(Map.of(
                            Constants.MetaKey.SEND_USER_ID, vo.getProperties().get(Constants.MetaKey.SEND_USER_ID),
                            Constants.MetaKey.SEND_USER_NAME, vo.getProperties().get(Constants.MetaKey.SEND_USER_NAME),
                            Constants.MetaKey.RECEIVE_USER_ID, vo.getProperties().get(Constants.MetaKey.RECEIVE_USER_ID)
                    ))
                    .build();
        } catch (Exception e) {
            throw new CIMException("Failed to create OfflineMsg from SaveOfflineMsgReqVO", e);
        }
    }

}
