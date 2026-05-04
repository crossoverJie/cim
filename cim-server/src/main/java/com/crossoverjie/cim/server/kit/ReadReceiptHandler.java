package com.crossoverjie.cim.server.kit;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.ConversationReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.GroupMsgReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PMsgReadReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ReadReceiptHandler {

    @Resource
    private RouteApi routeApi;

    public void handleP2PMsgRead(Request request) {
        Map<String, String> properties = request.getPropertiesMap();
        
        Long messageId = parseLong(properties.get(Constants.MetaKey.MESSAGE_ID));
        Long userId = request.getRequestId();
        Long sendUserId = parseLong(properties.get(Constants.MetaKey.SEND_USER_ID));
        String conversationId = properties.get(Constants.MetaKey.CONVERSATION_ID);
        
        if (messageId == null || userId == null) {
            log.error("Invalid P2P msg read request: messageId={}, userId={}", messageId, userId);
            return;
        }
        
        P2PMsgReadReqVO reqVO = P2PMsgReadReqVO.builder()
                .messageId(messageId)
                .userId(userId)
                .sendUserId(sendUserId)
                .conversationId(conversationId)
                .build();
        
        try {
            routeApi.p2pMsgRead(reqVO);
            log.info("Handled P2P msg read: messageId={}, userId={}", messageId, userId);
        } catch (Exception e) {
            log.error("Failed to handle P2P msg read", e);
        }
    }

    public void handleConversationRead(Request request) {
        Map<String, String> properties = request.getPropertiesMap();
        
        Long userId = request.getRequestId();
        String conversationId = properties.get(Constants.MetaKey.CONVERSATION_ID);
        Integer conversationType = parseInteger(properties.get(Constants.MetaKey.CONVERSATION_TYPE));
        Long beforeMessageId = parseLong(properties.get(Constants.MetaKey.BEFORE_MESSAGE_ID));
        Long targetUserId = parseLong(properties.get(Constants.MetaKey.RECEIVE_USER_ID));
        Long groupId = parseLong(properties.get(Constants.MetaKey.GROUP_ID));
        
        if (userId == null || conversationId == null || conversationType == null) {
            log.error("Invalid conversation read request: userId={}, conversationId={}, conversationType={}", 
                    userId, conversationId, conversationType);
            return;
        }
        
        ConversationReadReqVO reqVO = ConversationReadReqVO.builder()
                .userId(userId)
                .conversationId(conversationId)
                .conversationType(conversationType)
                .beforeMessageId(beforeMessageId)
                .targetUserId(targetUserId)
                .groupId(groupId)
                .build();
        
        try {
            routeApi.conversationRead(reqVO);
            log.info("Handled conversation read: userId={}, conversationId={}", userId, conversationId);
        } catch (Exception e) {
            log.error("Failed to handle conversation read", e);
        }
    }

    public void handleGroupMsgRead(Request request) {
        Map<String, String> properties = request.getPropertiesMap();
        
        Long messageId = parseLong(properties.get(Constants.MetaKey.MESSAGE_ID));
        Long groupId = parseLong(properties.get(Constants.MetaKey.GROUP_ID));
        Long userId = request.getRequestId();
        Integer totalMembers = parseInteger(properties.get(Constants.MetaKey.TOTAL_MEMBERS));
        
        if (messageId == null || groupId == null || userId == null) {
            log.error("Invalid group msg read request: messageId={}, groupId={}, userId={}", 
                    messageId, groupId, userId);
            return;
        }
        
        GroupMsgReadReqVO reqVO = GroupMsgReadReqVO.builder()
                .messageId(messageId)
                .groupId(groupId)
                .userId(userId)
                .totalMembers(totalMembers)
                .build();
        
        try {
            routeApi.groupMsgRead(reqVO);
            log.info("Handled group msg read: messageId={}, groupId={}, userId={}", messageId, groupId, userId);
        } catch (Exception e) {
            log.error("Failed to handle group msg read", e);
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
