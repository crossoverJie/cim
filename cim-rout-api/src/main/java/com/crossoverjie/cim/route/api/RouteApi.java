package com.crossoverjie.cim.route.api;

import com.crossoverjie.cim.common.core.proxy.Request;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.ConversationReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.GroupMsgReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.req.OfflineMsgReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PMsgReadReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.req.RegisterInfoReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.GroupReadCountResVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;
import com.crossoverjie.cim.route.api.vo.res.UnreadCountResVO;

import java.util.List;
import java.util.Set;

public interface RouteApi {

    BaseResponse<NULLBody> groupRoute(ChatReqVO groupReqVO);

    BaseResponse<NULLBody> p2pRoute(P2PReqVO p2pRequest);

    BaseResponse<NULLBody> offLine(ChatReqVO groupReqVO);

    BaseResponse<CIMServerResVO> login(LoginReqVO loginReqVO) throws Exception;

    BaseResponse<RegisterInfoResVO> registerAccount(RegisterInfoReqVO registerInfoReqVO) throws Exception;

    @Request(method = Request.GET)
    BaseResponse<Set<CIMUserInfo>> onlineUser() throws Exception;

    BaseResponse<NULLBody> fetchOfflineMsgs(OfflineMsgReqVO offlineMsgReqVO);

    BaseResponse<NULLBody> p2pMsgRead(P2PMsgReadReqVO reqVO);

    BaseResponse<NULLBody> conversationRead(ConversationReadReqVO reqVO);

    BaseResponse<NULLBody> groupMsgRead(GroupMsgReadReqVO reqVO);

    @Request(method = Request.GET)
    BaseResponse<UnreadCountResVO> getP2PUnreadCount(
            @Request.Param("userId") Long userId,
            @Request.Param("conversationId") String conversationId);

    @Request(method = Request.GET)
    BaseResponse<UnreadCountResVO> getGroupUnreadCount(
            @Request.Param("userId") Long userId,
            @Request.Param("groupId") Long groupId);

    @Request(method = Request.GET)
    BaseResponse<GroupReadCountResVO> getGroupMsgReadCount(
            @Request.Param("groupId") Long groupId,
            @Request.Param("messageId") Long messageId);

    BaseResponse<List<GroupReadCountResVO>> getGroupMsgsReadCount(
            @Request.Param("groupId") Long groupId,
            List<Long> messageIds);
}
