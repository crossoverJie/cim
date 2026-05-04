package com.crossoverjie.cim.persistence.api.service;

import com.crossoverjie.cim.persistence.api.pojo.GroupMsgReadCount;
import com.crossoverjie.cim.persistence.api.pojo.GroupMsgReadStatus;
import com.crossoverjie.cim.persistence.api.pojo.P2PMsgReadStatus;

import java.util.List;

public interface ReadReceiptService {

    void saveP2PMsgReadStatus(P2PMsgReadStatus status);

    void saveP2PMsgReadStatusBatch(List<P2PMsgReadStatus> statusList);

    P2PMsgReadStatus getP2PMsgReadStatus(Long messageId, Long receiveUserId);

    List<P2PMsgReadStatus> getUnreadP2PMsgs(Long receiveUserId, String conversationId);

    void markP2PMsgAsRead(Long messageId, Long receiveUserId);

    void markP2PConversationAsRead(Long receiveUserId, String conversationId, Long beforeMessageId);

    long countUnreadP2PMsgs(Long receiveUserId, String conversationId);

    void saveGroupMsgReadStatus(GroupMsgReadStatus status);

    void saveGroupMsgReadStatusBatch(List<GroupMsgReadStatus> statusList);

    GroupMsgReadStatus getGroupMsgReadStatus(Long messageId, Long userId);

    List<GroupMsgReadStatus> getUnreadGroupMsgs(Long userId, Long groupId);

    void markGroupMsgAsRead(Long messageId, Long groupId, Long userId);

    void incrementGroupReadCount(Long groupId, Long messageId, int totalMembers);

    GroupMsgReadCount getGroupReadCount(Long groupId, Long messageId);

    List<GroupMsgReadCount> getGroupReadCounts(Long groupId, List<Long> messageIds);
}
