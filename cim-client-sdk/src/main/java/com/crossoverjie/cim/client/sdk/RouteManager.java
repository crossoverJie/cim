package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientImpl;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.core.proxy.RpcProxyManager;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.ConversationReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.GroupMsgReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.req.OfflineMsgReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PMsgReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.GroupReadCountResVO;
import com.crossoverjie.cim.route.api.vo.res.UnreadCountResVO;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import okhttp3.OkHttpClient;

public class RouteManager {


    private final RouteApi routeApi;
    private final Event event;

    public RouteManager(String routeUrl, OkHttpClient okHttpClient, Event event) {
        routeApi = RpcProxyManager.create(RouteApi.class, routeUrl, okHttpClient);
        this.event = event;
    }

    public CIMServerResVO getServer(LoginReqVO loginReqVO) throws Exception {
        BaseResponse<CIMServerResVO> cimServerResVO = routeApi.login(loginReqVO);

        if (!cimServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())) {
            event.info(cimServerResVO.getMessage());

            if (ClientImpl.getClient().getState() == ClientState.State.Reconnecting) {
                event.warn("###{}###", StatusEnum.RECONNECT_FAIL.getMessage());
                throw new CIMException(StatusEnum.RECONNECT_FAIL);
            }
        }
        return cimServerResVO.getDataBody();
    }

    public CompletableFuture<Void> sendP2P(CompletableFuture<Void> future, P2PReqVO p2PReqVO) {
        return CompletableFuture.runAsync(() -> {
            try {
                BaseResponse<NULLBody> response = routeApi.p2pRoute(p2PReqVO);
                if (response.getCode().equals(StatusEnum.OFF_LINE.getCode())) {
                    future.completeExceptionally(new CIMException(StatusEnum.OFF_LINE));
                }
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
                event.error("send p2p msg error", e);
            }
        });
    }

    public CompletableFuture<Void> sendGroupMsg(ChatReqVO chatReqVO) {
        return CompletableFuture.runAsync(() -> {
            try {
                routeApi.groupRoute(chatReqVO);
            } catch (Exception e) {
                event.error("send group msg error", e);
            }
        });
    }

    public void offLine(Long userId) {
        ChatReqVO vo = new ChatReqVO(userId, "offLine", null);
        routeApi.offLine(vo);
    }

    public Set<CIMUserInfo> onlineUser() throws Exception {
        BaseResponse<Set<CIMUserInfo>> onlineUsersResVO = routeApi.onlineUser();
        return onlineUsersResVO.getDataBody();
    }

    public void fetchOfflineMsgs(Long userId) {
        OfflineMsgReqVO offlineMsgReqVO = OfflineMsgReqVO.builder().receiveUserId(userId).build();
        routeApi.fetchOfflineMsgs(offlineMsgReqVO);
    }

    public CompletableFuture<Void> sendP2PMsgRead(Long userId, Long messageId, Long sendUserId, String conversationId) {
        return CompletableFuture.runAsync(() -> {
            try {
                P2PMsgReadReqVO reqVO = P2PMsgReadReqVO.builder()
                        .userId(userId)
                        .messageId(messageId)
                        .sendUserId(sendUserId)
                        .conversationId(conversationId)
                        .build();
                routeApi.p2pMsgRead(reqVO);
            } catch (Exception e) {
                event.error("send p2p msg read error", e);
            }
        });
    }

    public CompletableFuture<Void> sendConversationRead(Long userId, String conversationId, Integer conversationType,
                                                          Long beforeMessageId, Long targetUserId, Long groupId) {
        return CompletableFuture.runAsync(() -> {
            try {
                ConversationReadReqVO reqVO = ConversationReadReqVO.builder()
                        .userId(userId)
                        .conversationId(conversationId)
                        .conversationType(conversationType)
                        .beforeMessageId(beforeMessageId)
                        .targetUserId(targetUserId)
                        .groupId(groupId)
                        .build();
                routeApi.conversationRead(reqVO);
            } catch (Exception e) {
                event.error("send conversation read error", e);
            }
        });
    }

    public CompletableFuture<Void> sendGroupMsgRead(Long userId, Long messageId, Long groupId, Integer totalMembers) {
        return CompletableFuture.runAsync(() -> {
            try {
                GroupMsgReadReqVO reqVO = GroupMsgReadReqVO.builder()
                        .userId(userId)
                        .messageId(messageId)
                        .groupId(groupId)
                        .totalMembers(totalMembers)
                        .build();
                routeApi.groupMsgRead(reqVO);
            } catch (Exception e) {
                event.error("send group msg read error", e);
            }
        });
    }

    public UnreadCountResVO getP2PUnreadCount(Long userId, String conversationId) throws Exception {
        BaseResponse<UnreadCountResVO> response = routeApi.getP2PUnreadCount(userId, conversationId);
        return response.getDataBody();
    }

    public UnreadCountResVO getGroupUnreadCount(Long userId, Long groupId) throws Exception {
        BaseResponse<UnreadCountResVO> response = routeApi.getGroupUnreadCount(userId, groupId);
        return response.getDataBody();
    }

    public GroupReadCountResVO getGroupMsgReadCount(Long groupId, Long messageId) throws Exception {
        BaseResponse<GroupReadCountResVO> response = routeApi.getGroupMsgReadCount(groupId, messageId);
        return response.getDataBody();
    }

    public List<GroupReadCountResVO> getGroupMsgsReadCount(Long groupId, List<Long> messageIds) throws Exception {
        BaseResponse<List<GroupReadCountResVO>> response = routeApi.getGroupMsgsReadCount(groupId, messageIds);
        return response.getDataBody();
    }
}
