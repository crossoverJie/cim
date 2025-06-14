package com.crossoverjie.cim.persistence.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfflineMsgLastSendRecord {
    private Long userId;
    private String lastMessageId;
    private LocalDateTime updatedAt;
}
