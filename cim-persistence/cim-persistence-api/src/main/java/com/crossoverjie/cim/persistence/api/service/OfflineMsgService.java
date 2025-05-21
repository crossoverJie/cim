package com.crossoverjie.cim.persistence.api.service;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
public interface OfflineMsgService {

    /**
     * Save offline messages
     *
     * @param offlineMsg
     */
    void save(OfflineMsg offlineMsg);


    /**
     * After the client goes online, it retrieves messages in pages based on the cursor (the id of the last pushed offline message stored)
     *
     * @param receiveUserId
     * @param limit
     * @return
     */
    List<OfflineMsg> fetchOfflineMsgsWithCursor(Long receiveUserId, int limit);

    /**
     * After the client goes online, it retrieves the id of the offline messages stored in the database
     *
     * @param receiveUserId
     * @return messageIds
     */
    List<Long> fetchOfflineMsgIdsWithCursor(Long receiveUserId);

    /**
     * After the client goes online, it updates the status of offline messages
     *
     * @param receiveUserId
     * @param messageIds
     */
    void updateStatus(Long receiveUserId, List<Long> messageIds);

    /**
     * Batch insert offline messages
     *
     * @param offlineMsgs
     * @return
     */
    Integer insertBatch(List<OfflineMsg> offlineMsgs);
}
