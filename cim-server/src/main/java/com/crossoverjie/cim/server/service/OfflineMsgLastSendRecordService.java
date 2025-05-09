package com.crossoverjie.cim.server.service;

public interface OfflineMsgLastSendRecordService {

    /**
     * Save the last messageId of the latest batch of offline messages pushed to the client
     */
    void saveLatestMessageId(Long userId, String lastMessageId);

    /**
     * Obtain the latest messageId of a certain session
     *
     * @param userId
     * @return
     */
    String getLatestMessageId(Long userId);
}
