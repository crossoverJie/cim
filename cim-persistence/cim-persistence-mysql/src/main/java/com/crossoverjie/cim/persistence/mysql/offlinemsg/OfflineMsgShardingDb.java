package com.crossoverjie.cim.persistence.api.service;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;

import java.time.LocalDateTime;
import java.util.List;

public interface EnhancedOfflineMsgStore extends OfflineMsgStore {

    void saveBatch(List<OfflineMsg> offlineMsgs);

    List<OfflineMsg> fetchWithCursor(Long receiveUserId, Long lastMessageId, Integer limit);

    void deleteExpiredMessages(LocalDateTime expireTime);

    int countPendingMessages(Long receiveUserId);

    OfflineMsg findByMessageId(Long messageId);
}
