package com.crossoverjie.cim.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfflineMsgLastSendRecord {
    private Long userId;
    private String lastMessageId;
    private LocalDateTime updatedAt;
}
