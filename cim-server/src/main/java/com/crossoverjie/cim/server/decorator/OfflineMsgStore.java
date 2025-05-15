package com.crossoverjie.cim.server.decorator;

import com.crossoverjie.cim.server.pojo.OfflineMsg;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
public interface OfflineMsgStore {

    /**
     * Save offline message
     * @param offlineMsg
     */
    void save(OfflineMsg offlineMsg);

    /**
     * Fetch offline messages for a user
     * @param userId
     * @return
     */
    List<OfflineMsg> fetch(Long userId);

    /**
     * Mark messages as delivered
     * @param userId
     * @param messageIds
     */
    void markDelivered(Long userId, List<Long> messageIds);
}
