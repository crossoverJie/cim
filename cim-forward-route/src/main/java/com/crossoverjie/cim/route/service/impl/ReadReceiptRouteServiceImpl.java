package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.persistence.api.pojo.ConversationReadRecord;
import com.crossoverjie.cim.persistence.api.pojo.GroupMsgReadCount;
import com.crossoverjie.cim.persistence.api.pojo.GroupMsgReadStatus;
import com.crossoverjie.cim.persistence.api.pojo.P2PMsgReadStatus;
import com.crossoverjie.cim.persistence.api.service.ReadReceiptService;
import com.crossoverjie.cim.route.api.vo.req.ConversationReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.GroupMsgReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PMsgReadReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.GroupReadCountResVO;
import com.crossoverjie.cim.route.api.vo.res.UnreadCountResVO;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.ReadReceiptRouteService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.server.api.ServerApi;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ReadReceiptRouteServiceImpl implements ReadReceiptRouteService {

    @Resource
    private ReadReceiptService readReceiptService;

    @Resource
    private AccountService accountService;

    @Resource
    private UserInfoCacheService userInfoCacheService;

    @Resource
    private ServerApi serverApi;

    @Override
    public void handleP2PMsgRead(P2PMsgReadReqVO reqVO) {
        readReceiptService.markP2PMsgAsRead(reqVO.getMessageId(), reqVO.getUserId());

        P2PMsgReadStatus status = readReceiptService.getP2PMsgReadStatus(reqVO.getMessageId(), reqVO.getUserId());
        if (status == null) {
            status = P2PMsgReadStatus.builder()
                    .messageId(reqVO.getMessageId())
                    .sendUserId(reqVO.getSendUserId())
                    .receiveUserId(reqVO.getUserId())
                    .conversationId(reqVO.getConversationId())
                    .isRead(Constants.MSG_HAS_READ)
                    .readAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();
            readReceiptService.saveP2PMsgReadStatus(status);
        }

        pushReadReceiptToSender(reqVO.getMessageId(), reqVO.getUserId(), reqVO.getSendUserId());
    }

    @Override
    public void handleConversationRead(ConversationReadReqVO reqVO) {
        if (reqVO.getConversationType() == Constants.CONVERSATION_TYPE_P2P) {
            handleP2PConversationRead(reqVO);
        } else if (reqVO.getConversationType() == Constants.CONVERSATION_TYPE_GROUP) {
            handleGroupConversationRead(reqVO);
        }
    }

    private void handleP2PConversationRead(ConversationReadReqVO reqVO) {
        String conversationId = generateP2PConversationId(reqVO.getUserId(), reqVO.getTargetUserId());
        
        if (reqVO.getBeforeMessageId() == null) {
            reqVO.setBeforeMessageId(Long.MAX_VALUE);
        }
        
        readReceiptService.markP2PConversationAsRead(reqVO.getUserId(), conversationId, reqVO.getBeforeMessageId());
        pushConversationReadToTarget(reqVO.getUserId(), reqVO.getTargetUserId(), conversationId, reqVO.getBeforeMessageId());
    }

    private void handleGroupConversationRead(ConversationReadReqVO reqVO) {
        if (reqVO.getBeforeMessageId() == null) {
            reqVO.setBeforeMessageId(Long.MAX_VALUE);
        }
        
        List<GroupMsgReadStatus> unreadMsgs = readReceiptService.getUnreadGroupMsgs(reqVO.getUserId(), reqVO.getGroupId());
        if (unreadMsgs != null && !unreadMsgs.isEmpty()) {
            for (GroupMsgReadStatus msg : unreadMsgs) {
                if (msg.getMessageId() <= reqVO.getBeforeMessageId()) {
                    readReceiptService.markGroupMsgAsRead(msg.getMessageId(), reqVO.getGroupId(), reqVO.getUserId());
                    
                    int totalMembers = reqVO.getTotalMembers() != null ? reqVO.getTotalMembers() : 100;
                    readReceiptService.incrementGroupReadCount(reqVO.getGroupId(), msg.getMessageId(), totalMembers);
                    
                    pushGroupReadCount(reqVO.getGroupId(), msg.getMessageId());
                }
            }
        }
    }

    @Override
    public void handleGroupMsgRead(GroupMsgReadReqVO reqVO) {
        readReceiptService.markGroupMsgAsRead(reqVO.getMessageId(), reqVO.getGroupId(), reqVO.getUserId());
        
        GroupMsgReadStatus status = readReceiptService.getGroupMsgReadStatus(reqVO.getMessageId(), reqVO.getUserId());
        if (status == null) {
            status = GroupMsgReadStatus.builder()
                    .messageId(reqVO.getMessageId())
                    .groupId(reqVO.getGroupId())
                    .userId(reqVO.getUserId())
                    .isRead(Constants.MSG_HAS_READ)
                    .readAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();
            readReceiptService.saveGroupMsgReadStatus(status);
        }
        
        int totalMembers = reqVO.getTotalMembers() != null ? reqVO.getTotalMembers() : 100;
        readReceiptService.incrementGroupReadCount(reqVO.getGroupId(), reqVO.getMessageId(), totalMembers);
        
        pushGroupReadCount(reqVO.getGroupId(), reqVO.getMessageId());
    }

    @Override
    public UnreadCountResVO getP2PUnreadCount(Long userId, String conversationId) {
        long count = readReceiptService.countUnreadP2PMsgs(userId, conversationId);
        return UnreadCountResVO.builder()
                .userId(userId)
                .conversationId(conversationId)
                .conversationType(Constants.CONVERSATION_TYPE_P2P)
                .unreadCount((int) count)
                .build();
    }

    @Override
    public UnreadCountResVO getGroupUnreadCount(Long userId, Long groupId) {
        List<GroupMsgReadStatus> unreadMsgs = readReceiptService.getUnreadGroupMsgs(userId, groupId);
        int count = unreadMsgs != null ? unreadMsgs.size() : 0;
        return UnreadCountResVO.builder()
                .userId(userId)
                .groupId(groupId)
                .conversationType(Constants.CONVERSATION_TYPE_GROUP)
                .unreadCount(count)
                .build();
    }

    @Override
    public GroupReadCountResVO getGroupMsgReadCount(Long groupId, Long messageId) {
        GroupMsgReadCount count = readReceiptService.getGroupReadCount(groupId, messageId);
        if (count == null) {
            return GroupReadCountResVO.builder()
                    .groupId(groupId)
                    .messageId(messageId)
                    .readCount(0)
                    .totalMembers(0)
                    .build();
        }
        return GroupReadCountResVO.builder()
                .groupId(count.getGroupId())
                .messageId(count.getMessageId())
                .readCount(count.getReadCount())
                .totalMembers(count.getTotalMembers())
                .build();
    }

    @Override
    public List<GroupReadCountResVO> getGroupMsgsReadCount(Long groupId, List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<GroupMsgReadCount> counts = readReceiptService.getGroupReadCounts(groupId, messageIds);
        List<GroupReadCountResVO> result = new ArrayList<>();
        
        for (Long messageId : messageIds) {
            GroupReadCountResVO resVO = counts.stream()
                    .filter(c -> c.getMessageId().equals(messageId))
                    .findFirst()
                    .map(c -> GroupReadCountResVO.builder()
                            .groupId(c.getGroupId())
                            .messageId(c.getMessageId())
                            .readCount(c.getReadCount())
                            .totalMembers(c.getTotalMembers())
                            .build())
                    .orElse(GroupReadCountResVO.builder()
                            .groupId(groupId)
                            .messageId(messageId)
                            .readCount(0)
                            .totalMembers(0)
                            .build());
            result.add(resVO);
        }
        
        return result;
    }

    private void pushReadReceiptToSender(Long messageId, Long readerUserId, Long sendUserId) {
        Optional<CIMServerResVO> senderServerOpt = accountService.loadRouteRelatedByUserId(sendUserId);
        
        if (senderServerOpt.isEmpty()) {
            log.info("Sender userId={} is offline, read receipt will be delivered when online", sendUserId);
            return;
        }
        
        CIMServerResVO senderServer = senderServerOpt.get();
        Optional<CIMUserInfo> readerInfoOpt = userInfoCacheService.loadUserInfoByUserId(readerUserId);
        
        String url = "http://" + senderServer.getIp() + ":" + senderServer.getHttpPort();
        
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.MetaKey.MESSAGE_ID, String.valueOf(messageId));
        properties.put(Constants.MetaKey.READER_USER_ID, String.valueOf(readerUserId));
        properties.put(Constants.MetaKey.READ_TYPE, String.valueOf(Constants.READ_TYPE_SINGLE));
        
        readerInfoOpt.ifPresent(info -> 
            properties.put(Constants.MetaKey.READER_USER_NAME, info.getUserName())
        );
        
        SendMsgReqVO msgReqVO = SendMsgReqVO.builder()
                .userId(sendUserId)
                .cmd(BaseCommand.READ_RECEIPT_PUSH)
                .properties(properties)
                .build();
        
        try {
            serverApi.sendMsg(msgReqVO, url);
            log.info("Pushed read receipt to sender userId={}, messageId={}", sendUserId, messageId);
        } catch (Exception e) {
            log.error("Failed to push read receipt to sender userId={}", sendUserId, e);
        }
    }

    private void pushConversationReadToTarget(Long userId, Long targetUserId, String conversationId, Long beforeMessageId) {
        Optional<CIMServerResVO> targetServerOpt = accountService.loadRouteRelatedByUserId(targetUserId);
        
        if (targetServerOpt.isEmpty()) {
            log.info("Target userId={} is offline, conversation read receipt will be delivered when online", targetUserId);
            return;
        }
        
        CIMServerResVO targetServer = targetServerOpt.get();
        Optional<CIMUserInfo> userInfoOpt = userInfoCacheService.loadUserInfoByUserId(userId);
        
        String url = "http://" + targetServer.getIp() + ":" + targetServer.getHttpPort();
        
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.MetaKey.READER_USER_ID, String.valueOf(userId));
        properties.put(Constants.MetaKey.CONVERSATION_ID, conversationId);
        properties.put(Constants.MetaKey.READ_TYPE, String.valueOf(Constants.READ_TYPE_CONVERSATION));
        properties.put(Constants.MetaKey.BEFORE_MESSAGE_ID, String.valueOf(beforeMessageId));
        
        userInfoOpt.ifPresent(info -> 
            properties.put(Constants.MetaKey.READER_USER_NAME, info.getUserName())
        );
        
        SendMsgReqVO msgReqVO = SendMsgReqVO.builder()
                .userId(targetUserId)
                .cmd(BaseCommand.READ_RECEIPT_PUSH)
                .properties(properties)
                .build();
        
        try {
            serverApi.sendMsg(msgReqVO, url);
            log.info("Pushed conversation read receipt to target userId={}, conversationId={}", targetUserId, conversationId);
        } catch (Exception e) {
            log.error("Failed to push conversation read receipt to target userId={}", targetUserId, e);
        }
    }

    private void pushGroupReadCount(Long groupId, Long messageId) {
        GroupMsgReadCount readCount = readReceiptService.getGroupReadCount(groupId, messageId);
        if (readCount == null) {
            return;
        }
        
        Map<Long, CIMServerResVO> routes = accountService.loadRouteRelated();
        
        Map<String, String> properties = new HashMap<>();
        properties.put(Constants.MetaKey.GROUP_ID, String.valueOf(groupId));
        properties.put(Constants.MetaKey.MESSAGE_ID, String.valueOf(messageId));
        properties.put(Constants.MetaKey.READ_COUNT, String.valueOf(readCount.getReadCount()));
        properties.put(Constants.MetaKey.TOTAL_MEMBERS, String.valueOf(readCount.getTotalMembers()));
        
        for (Map.Entry<Long, CIMServerResVO> entry : routes.entrySet()) {
            CIMServerResVO server = entry.getValue();
            String url = "http://" + server.getIp() + ":" + server.getHttpPort();
            
            SendMsgReqVO msgReqVO = SendMsgReqVO.builder()
                    .userId(entry.getKey())
                    .cmd(BaseCommand.GROUP_READ_COUNT_PUSH)
                    .properties(properties)
                    .build();
            
            try {
                serverApi.sendMsg(msgReqVO, url);
            } catch (Exception e) {
                log.error("Failed to push group read count to userId={}", entry.getKey(), e);
            }
        }
        
        log.info("Pushed group read count to groupId={}, messageId={}, readCount={}", 
                groupId, messageId, readCount.getReadCount());
    }

    private String generateP2PConversationId(Long userId1, Long userId2) {
        if (userId1 < userId2) {
            return "p2p" + Constants.CONVERSATION_ID_SEPARATOR + userId1 + Constants.CONVERSATION_ID_SEPARATOR + userId2;
        } else {
            return "p2p" + Constants.CONVERSATION_ID_SEPARATOR + userId2 + Constants.CONVERSATION_ID_SEPARATOR + userId1;
        }
    }
}
