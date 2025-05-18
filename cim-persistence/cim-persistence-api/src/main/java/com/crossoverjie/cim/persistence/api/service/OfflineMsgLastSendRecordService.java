package com.crossoverjie.cim.persistence.api.service;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
public interface OfflineMsgLastSendRecordService {

    /**
     * Save the last messageId of the latest batch of offline messages pushed to the client
     */
    void saveLatestMessageId(Long userId, Long lastMessageId);

    /**
     * Obtain the latest messageId of a certain session
     *
     * @param userId
     * @return
     */
    String getLatestMessageId(Long userId);
}
