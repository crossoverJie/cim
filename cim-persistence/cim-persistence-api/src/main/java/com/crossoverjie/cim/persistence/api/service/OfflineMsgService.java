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
     * @param userId
     * @param limit
     * @return
     */
    List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, int limit);

    /**
     * After the client goes online, it retrieves the id of the offline messages stored in the database
     *
     * @param userId
     * @return messageIds
     */
    List<Long> fetchOfflineMsgIdsWithCursor(Long userId);

    /**
     * After the client goes online, it updates the status of offline messages
     *
     * @param userId
     * @param messageIds
     */
    void updateStatus(Long userId, List<Long> messageIds);

    /**
     * Batch insert offline messages
     *
     * @param offlineMsgs
     * @return
     */
    Integer insertBatch(List<OfflineMsg> offlineMsgs);
}
