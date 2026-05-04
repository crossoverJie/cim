package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientBuilderImpl;
import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.GroupReadCountResVO;
import com.crossoverjie.cim.route.api.vo.res.UnreadCountResVO;
import java.io.Closeable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface Client extends Closeable {

    static ClientBuilder builder() {
        return new ClientBuilderImpl();
    }

    default void sendP2P(P2PReqVO p2PReqVO) throws Exception {
        sendP2PAsync(p2PReqVO).get();
    }

    CompletableFuture<Void> sendP2PAsync(P2PReqVO p2PReqVO);

    default void sendGroup(String msg) throws Exception {
        sendGroupAsync(msg).get();
    }

    CompletableFuture<Void> sendGroupAsync(String msg);

    default void sendP2PMsgRead(Long messageId, Long sendUserId, String conversationId) throws Exception {
        sendP2PMsgReadAsync(messageId, sendUserId, conversationId).get();
    }

    CompletableFuture<Void> sendP2PMsgReadAsync(Long messageId, Long sendUserId, String conversationId);

    default void sendConversationRead(String conversationId, Integer conversationType, 
                                       Long beforeMessageId, Long targetUserId, Long groupId) throws Exception {
        sendConversationReadAsync(conversationId, conversationType, beforeMessageId, targetUserId, groupId).get();
    }

    CompletableFuture<Void> sendConversationReadAsync(String conversationId, Integer conversationType,
                                                        Long beforeMessageId, Long targetUserId, Long groupId);

    default void sendGroupMsgRead(Long messageId, Long groupId, Integer totalMembers) throws Exception {
        sendGroupMsgReadAsync(messageId, groupId, totalMembers).get();
    }

    CompletableFuture<Void> sendGroupMsgReadAsync(Long messageId, Long groupId, Integer totalMembers);

    UnreadCountResVO getP2PUnreadCount(String conversationId) throws Exception;

    UnreadCountResVO getGroupUnreadCount(Long groupId) throws Exception;

    GroupReadCountResVO getGroupMsgReadCount(Long groupId, Long messageId) throws Exception;

    List<GroupReadCountResVO> getGroupMsgsReadCount(Long groupId, List<Long> messageIds) throws Exception;

    ClientState.State getState();

    ClientConfigurationData.Auth getAuth();

    Set<CIMUserInfo> getOnlineUser() throws Exception;

    Optional<CIMServerResVO> getServerInfo();

}
