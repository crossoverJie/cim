package com.crossoverjie.cim.msg.storage.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class OfflineMessage {
    private Long id;
    private String conversationId;
    private String messageId;
    private String senderId;
    private String receiverId;
    private byte[] content;
    private Integer messageType;
    private Integer status;        // 0: Pending, 1: Acked
    private LocalDateTime createdAt;
    private Map<String, String> properties;
}
