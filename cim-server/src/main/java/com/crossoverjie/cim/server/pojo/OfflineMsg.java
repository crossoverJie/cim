package com.crossoverjie.cim.server.pojo;

import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfflineMsg {
    private Long messageId;
    private Long userId;
    private byte[] content;
    private Integer messageType;   // 0: Text, 1: Image
    private Integer status;        // 0: Pending, 1: Acked
    private LocalDateTime createdAt;
    /**
     * 消息来源存储在这里
     */
    private Map<String, String> properties;

}
