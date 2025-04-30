package com.crossoverjie.cim.msg.storage.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfflineMsgLastSendRecord {
    private String conversationId;
    //p2p中的消息接收者，群聊(1001, 'group:12345', 98765)
    private String userId;
    private String lastMessageId;
    private LocalDateTime updatedAt;
}
