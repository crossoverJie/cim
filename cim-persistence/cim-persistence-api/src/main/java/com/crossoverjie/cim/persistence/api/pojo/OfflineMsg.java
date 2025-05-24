package com.crossoverjie.cim.persistence.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfflineMsg {
    private Long messageId;
    private Long receiveUserId;
    //todo 如果是图片的话，存储和取数怎么处理
    private String content;
    private Integer messageType;   // 0: Text, 1: Image
    private Integer status;        // 0: Pending, 1: Acked
    private LocalDateTime createdAt;
    /**
     * 消息来源存储在这里
     */
    private Map<String, String> properties;

}
