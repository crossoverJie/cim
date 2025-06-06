package com.crossoverjie.cim.persistence.api.service;


import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
public interface OfflineMsgStore {

    /**
     * Save offline message
     *
     * @param offlineMsg
     */
    void save(OfflineMsg offlineMsg);

    /**
     * Fetch offline messages for a user
     *
     * @param userId
     * @return
     */
    List<OfflineMsg> fetch(Long userId);

    /**
     * Mark messages as delivered
     *
     * @param userId
     * @param messageIds
     */
    void markDelivered(Long userId, List<Long> messageIds);
}
