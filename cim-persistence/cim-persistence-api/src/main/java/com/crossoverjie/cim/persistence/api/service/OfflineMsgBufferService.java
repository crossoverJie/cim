package com.crossoverjie.cim.persistence.api.service;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
public interface OfflineMsgBufferService {


    /**
     * Obtain all the offline messages of the user
     * @param userId
     * @param includeAcked if true, get all messages; if false, only include pending messages
     * @return
     */
    List<OfflineMsg> getOfflineMsgs(Long userId, boolean includeAcked);

    /**
     * Compensation: Background scheduled tasks, consumed from Buffer and stored in the database
     */
    void startOfflineMsgsBufferConsume();

    /**
     * Before writing: Write the offline message to buffer
     *
     * @param msg
     */
    void saveOfflineMsgInBuffer(OfflineMsg msg);

    /**
     * Delete offline messages from buffer
     *
     * @param messageId
     */
    void deleteOfflineMsgFromBuffer(Long messageId);

    /**
     * Mark the message as delivered
     *
     * @param messageId
     */
    void markDelivered(Long messageId);


    /**
     * Migrate offline messages to the database
     *
     * @param userId
     */
    void migrateOfflineMsgToDb(Long userId);
}
