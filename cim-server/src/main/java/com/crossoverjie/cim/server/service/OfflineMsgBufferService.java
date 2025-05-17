package com.crossoverjie.cim.server.service;


import com.crossoverjie.cim.server.pojo.OfflineMsg;

import java.util.List;

public interface OfflineMsgBufferService {


    /**
     * Obtain all the offline messages of the user
     *
     * @param userId
     * @return
     */
    List<OfflineMsg> getOfflineMsgs(Long userId);

    /**
     * Compensation: Background scheduled tasks, consumed from Buffer and stored in the database
     */
    void startOfflineMsgsBufferConsume();

    /**
     * Before writing: Write the offline message to buffer
     * @param msg
     */
    void saveOfflineMsgInBuffer(OfflineMsg msg);

    /**
     * Delete offline messages from buffer
     * @param messageId
     */
    void deleteOfflineMsgFromBuffer(Long messageId);

    /**
     * Mark the message as delivered
     * @param messageId
     */
    void markDelivered(Long messageId);


    /**
     * Migrate offline messages to the database
     * @param userId
     */
    void migrateOfflineMsgToDb(Long userId);
}
