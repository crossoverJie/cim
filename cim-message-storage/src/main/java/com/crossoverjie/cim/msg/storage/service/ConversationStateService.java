package com.crossoverjie.cim.msg.storage.service;

public interface ConversationStateService {

    /**
     * 保存最新推送到客户端的 messageId
     */
    void saveLatestMessageId(String conversationId, String userId, String messageId);

    /**
     * 获取某个会话最新的 messageId
     *
     * @param conversationId 会话id
     * @param userId         消息接受者id
     * @return
     */
    String getLatestMessageId(String conversationId, String userId);
}
