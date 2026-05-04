package com.crossoverjie.cim.persistence.mysql.readreceipt;

import com.crossoverjie.cim.persistence.api.pojo.ConversationReadRecord;
import com.crossoverjie.cim.persistence.api.pojo.GroupMsgReadCount;
import com.crossoverjie.cim.persistence.api.pojo.GroupMsgReadStatus;
import com.crossoverjie.cim.persistence.api.pojo.P2PMsgReadStatus;
import com.crossoverjie.cim.persistence.api.service.ReadReceiptService;
import com.crossoverjie.cim.persistence.mysql.readreceipt.mapper.ConversationReadRecordMapper;
import com.crossoverjie.cim.persistence.mysql.readreceipt.mapper.GroupMsgReadCountMapper;
import com.crossoverjie.cim.persistence.mysql.readreceipt.mapper.GroupMsgReadStatusMapper;
import com.crossoverjie.cim.persistence.mysql.readreceipt.mapper.P2PMsgReadStatusMapper;
import com.crossoverjie.cim.persistence.mysql.sharding.ShardingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReadReceiptServiceImpl implements ReadReceiptService {

    private final P2PMsgReadStatusMapper p2PMsgReadStatusMapper;
    private final GroupMsgReadStatusMapper groupMsgReadStatusMapper;
    private final GroupMsgReadCountMapper groupMsgReadCountMapper;
    private final ConversationReadRecordMapper conversationReadRecordMapper;

    public ReadReceiptServiceImpl(P2PMsgReadStatusMapper p2PMsgReadStatusMapper,
                                   GroupMsgReadStatusMapper groupMsgReadStatusMapper,
                                   GroupMsgReadCountMapper groupMsgReadCountMapper,
                                   ConversationReadRecordMapper conversationReadRecordMapper) {
        this.p2PMsgReadStatusMapper = p2PMsgReadStatusMapper;
        this.groupMsgReadStatusMapper = groupMsgReadStatusMapper;
        this.groupMsgReadCountMapper = groupMsgReadCountMapper;
        this.conversationReadRecordMapper = conversationReadRecordMapper;
    }

    @Override
    @Transactional
    public void saveP2PMsgReadStatus(P2PMsgReadStatus status) {
        String tableName = ShardingStrategy.getP2PMsgReadTableName(status.getReceiveUserId());
        p2PMsgReadStatusMapper.insert(tableName, status);
    }

    @Override
    @Transactional
    public void saveP2PMsgReadStatusBatch(List<P2PMsgReadStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return;
        }
        String tableName = ShardingStrategy.getP2PMsgReadTableName(statusList.get(0).getReceiveUserId());
        p2PMsgReadStatusMapper.insertBatch(tableName, statusList);
    }

    @Override
    public P2PMsgReadStatus getP2PMsgReadStatus(Long messageId, Long receiveUserId) {
        String tableName = ShardingStrategy.getP2PMsgReadTableName(receiveUserId);
        return p2PMsgReadStatusMapper.selectByMessageIdAndUserId(tableName, messageId, receiveUserId);
    }

    @Override
    public List<P2PMsgReadStatus> getUnreadP2PMsgs(Long receiveUserId, String conversationId) {
        String tableName = ShardingStrategy.getP2PMsgReadTableName(receiveUserId);
        return p2PMsgReadStatusMapper.selectUnreadByConversation(tableName, receiveUserId, conversationId);
    }

    @Override
    @Transactional
    public void markP2PMsgAsRead(Long messageId, Long receiveUserId) {
        String tableName = ShardingStrategy.getP2PMsgReadTableName(receiveUserId);
        p2PMsgReadStatusMapper.updateReadStatus(tableName, messageId, receiveUserId);
    }

    @Override
    @Transactional
    public void markP2PConversationAsRead(Long receiveUserId, String conversationId, Long beforeMessageId) {
        String tableName = ShardingStrategy.getP2PMsgReadTableName(receiveUserId);
        p2PMsgReadStatusMapper.updateReadStatusByConversation(tableName, receiveUserId, conversationId, beforeMessageId);

        ConversationReadRecord record = ConversationReadRecord.builder()
                .userId(receiveUserId)
                .conversationId(conversationId)
                .lastReadMessageId(beforeMessageId)
                .readAt(LocalDateTime.now())
                .build();
        String convTableName = ShardingStrategy.getConversationReadTableName(receiveUserId);
        conversationReadRecordMapper.upsert(convTableName, record);
    }

    @Override
    public long countUnreadP2PMsgs(Long receiveUserId, String conversationId) {
        String tableName = ShardingStrategy.getP2PMsgReadTableName(receiveUserId);
        return p2PMsgReadStatusMapper.countUnreadByConversation(tableName, receiveUserId, conversationId);
    }

    @Override
    @Transactional
    public void saveGroupMsgReadStatus(GroupMsgReadStatus status) {
        String tableName = ShardingStrategy.getGroupMsgReadTableName(status.getGroupId());
        groupMsgReadStatusMapper.insert(tableName, status);
    }

    @Override
    @Transactional
    public void saveGroupMsgReadStatusBatch(List<GroupMsgReadStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return;
        }
        String tableName = ShardingStrategy.getGroupMsgReadTableName(statusList.get(0).getGroupId());
        groupMsgReadStatusMapper.insertBatch(tableName, statusList);
    }

    @Override
    public GroupMsgReadStatus getGroupMsgReadStatus(Long messageId, Long userId) {
        Long groupId = extractGroupIdFromMessageId(messageId);
        String tableName = ShardingStrategy.getGroupMsgReadTableName(groupId);
        return groupMsgReadStatusMapper.selectByMessageIdAndUserId(tableName, messageId, userId);
    }

    @Override
    public List<GroupMsgReadStatus> getUnreadGroupMsgs(Long userId, Long groupId) {
        String tableName = ShardingStrategy.getGroupMsgReadTableName(groupId);
        return groupMsgReadStatusMapper.selectUnreadByGroup(tableName, userId, groupId);
    }

    @Override
    @Transactional
    public void markGroupMsgAsRead(Long messageId, Long groupId, Long userId) {
        String tableName = ShardingStrategy.getGroupMsgReadTableName(groupId);
        groupMsgReadStatusMapper.updateReadStatus(tableName, messageId, userId);
    }

    @Override
    @Transactional
    public void incrementGroupReadCount(Long groupId, Long messageId, int totalMembers) {
        String tableName = ShardingStrategy.getGroupMsgReadCountTableName(groupId);
        GroupMsgReadCount existing = groupMsgReadCountMapper.selectByMessageId(tableName, groupId, messageId);
        
        if (existing == null) {
            GroupMsgReadCount count = GroupMsgReadCount.builder()
                    .groupId(groupId)
                    .messageId(messageId)
                    .readCount(1)
                    .totalMembers(totalMembers)
                    .build();
            groupMsgReadCountMapper.insert(tableName, count);
        } else {
            groupMsgReadCountMapper.incrementReadCount(tableName, groupId, messageId);
        }
    }

    @Override
    public GroupMsgReadCount getGroupReadCount(Long groupId, Long messageId) {
        String tableName = ShardingStrategy.getGroupMsgReadCountTableName(groupId);
        return groupMsgReadCountMapper.selectByMessageId(tableName, groupId, messageId);
    }

    @Override
    public List<GroupMsgReadCount> getGroupReadCounts(Long groupId, List<Long> messageIds) {
        String tableName = ShardingStrategy.getGroupMsgReadCountTableName(groupId);
        return groupMsgReadCountMapper.selectByMessageIds(tableName, groupId, messageIds);
    }

    private Long extractGroupIdFromMessageId(Long messageId) {
        return messageId;
    }
}
