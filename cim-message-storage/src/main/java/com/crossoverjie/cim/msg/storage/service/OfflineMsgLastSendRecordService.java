package com.crossoverjie.cim.msg.storage.service;

public interface OfflineMsgLastSendRecordService {

    /**
     * Save the last messageId of the latest batch of offline messages pushed to the client
     */
    void saveLatestMessageId(String conversationId, String userId, String lastMessageId);

    /**
     * Obtain the latest messageId of a certain session
     *
     * @param conversationId
     * @param userId
     * @return
     */
    String getLatestMessageId(String conversationId, String userId);
}
