package com.crossoverjie.cim.route.controller;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import com.crossoverjie.cim.route.api.RouteApi;
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
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.CommonBizService;
import com.crossoverjie.cim.route.service.OfflineMsgService;
import com.crossoverjie.cim.route.service.ReadReceiptRouteService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.server.api.ServerApi;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.crossoverjie.cim.common.enums.StatusEnum.OFF_LINE;

@Slf4j
@Controller
@RequestMapping("/")
public class RouteController implements RouteApi {

    @Resource
    private MetaStore metaStore;

    @Resource
    private AccountService accountService;

    @Resource
    private UserInfoCacheService userInfoCacheService;

    @Resource
    private CommonBizService commonBizService;

    @Resource
    private RouteHandle routeHandle;

    @Resource
    private ServerApi serverApi;

    @Resource
    private OfflineMsgService offlineMsgService;

    @Resource
    private ReadReceiptRouteService readReceiptRouteService;


    @Operation(summary = "群聊 API")
    @RequestMapping(value = "groupRoute", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> groupRoute(@RequestBody ChatReqVO groupReqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();

        log.info("msg=[{}]", groupReqVO.toString());

        Map<Long, CIMServerResVO> serverResVoMap = accountService.loadRouteRelated();
        Set<Long> allUserIds = userInfoCacheService.getRegisteredUserIds();
        
        for (Long userId : allUserIds) {
            if (userId.equals(groupReqVO.getUserId())) {
                continue;
            }
            
            CIMServerResVO cimServerResVO = serverResVoMap.get(userId);
            ChatReqVO chatVO = new ChatReqVO(userId, groupReqVO.getMsg(), null);
            
            if (cimServerResVO != null) {
                accountService.pushMsg(cimServerResVO, groupReqVO.getUserId(), chatVO);
            } else {
                log.info("userId={} is offline, save group offline msg", userId);
                saveGroupOfflineMsg(groupReqVO, userId);
            }
        }

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    private void saveGroupOfflineMsg(ChatReqVO groupReqVO, Long receiveUserId) {
        Optional<CIMUserInfo> senderInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());
        
        senderInfo.ifPresent(sender -> {
            com.crossoverjie.cim.route.api.vo.req.P2PReqVO p2pReqVO = new P2PReqVO(
                    groupReqVO.getUserId(),
                    receiveUserId,
                    groupReqVO.getMsg()
            );
            
            Map<String, String> properties = new HashMap<>();
            properties.put(Constants.MetaKey.SEND_USER_ID, sender.getUserId().toString());
            properties.put(Constants.MetaKey.SEND_USER_NAME, sender.getUserName());
            properties.put(Constants.MetaKey.RECEIVE_USER_ID, receiveUserId.toString());
            properties.put(Constants.MetaKey.CONVERSATION_TYPE, String.valueOf(Constants.CONVERSATION_TYPE_GROUP));
            
            offlineMsgService.saveOfflineMsg(p2pReqVO);
        });
    }


    @Operation(summary = "私聊 API")
    @RequestMapping(value = "p2pRoute", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> p2pRoute(@RequestBody P2PReqVO p2pRequest) {
        BaseResponse<NULLBody> res = new BaseResponse();

        try {
            Optional<CIMServerResVO> cimServerResVO = accountService.loadRouteRelatedByUserId(p2pRequest.getReceiveUserId());
            if (cimServerResVO.isEmpty()) {
                log.warn("userId={} not online, save offline msg", p2pRequest.getReceiveUserId());
                offlineMsgService.saveOfflineMsg(p2pRequest);
                throw new CIMException(OFF_LINE);
            }

            ChatReqVO chatVO = new ChatReqVO(p2pRequest.getReceiveUserId(), p2pRequest.getMsg(), p2pRequest.getBatchMsg());
            accountService.pushMsg(cimServerResVO.get(), p2pRequest.getUserId(), chatVO);

            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());

        } catch (CIMException e) {
            res.setCode(e.getErrorCode());
            res.setMessage(e.getErrorMessage());
        }
        return res;
    }


    @Operation(summary = "客户端下线")
    @RequestMapping(value = "offLine", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> offLine(@RequestBody ChatReqVO chatReqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();

        Optional<CIMUserInfo> cimUserInfo = userInfoCacheService.loadUserInfoByUserId(chatReqVO.getUserId());

        cimUserInfo.ifPresent(userInfo -> {
            log.info("user [{}] offline!", userInfo);
            accountService.offLine(chatReqVO.getUserId());
        });

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    @Operation(summary = "登录并获取服务器")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<CIMServerResVO> login(@RequestBody LoginReqVO loginReqVO) throws Exception {
        BaseResponse<CIMServerResVO> res = new BaseResponse();
        StatusEnum status = accountService.login(loginReqVO);
        res.setCode(status.getCode());
        res.setMessage(status.getMessage());
        if (status != StatusEnum.SUCCESS) {
            return res;
        }

        Set<String> availableServerList = metaStore.getAvailableServerList();
        String key = String.valueOf(loginReqVO.getUserId());
        String server =
                routeHandle.routeServer(List.copyOf(availableServerList), key);
        log.info("userInfo=[{}] route server info=[{}]", loginReqVO, server);

        RouteInfo routeInfo = RouteInfoParseUtil.parse(server);
        routeInfo = commonBizService.checkServerAvailable(routeInfo, key);

        accountService.saveRouteInfo(loginReqVO, server);

        CIMServerResVO vo =
                new CIMServerResVO(routeInfo.getIp(), routeInfo.getCimServerPort(), routeInfo.getHttpPort());
        res.setDataBody(vo);
        return res;
    }

    @Operation(summary = "注册账号")
    @RequestMapping(value = "registerAccount", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<RegisterInfoResVO> registerAccount(@RequestBody RegisterInfoReqVO registerInfoReqVO)
            throws Exception {
        BaseResponse<RegisterInfoResVO> res = new BaseResponse();

        long userId = System.currentTimeMillis();
        RegisterInfoResVO info = new RegisterInfoResVO(userId, registerInfoReqVO.getUserName());
        info = accountService.register(info);

        res.setDataBody(info);
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    @Operation(summary = "获取所有在线用户")
    @RequestMapping(value = "onlineUser", method = RequestMethod.GET)
    @ResponseBody()
    @Override
    public BaseResponse<Set<CIMUserInfo>> onlineUser() throws Exception {
        BaseResponse<Set<CIMUserInfo>> res = new BaseResponse();

        Set<CIMUserInfo> cimUserInfos = userInfoCacheService.onlineUser();
        res.setDataBody(cimUserInfos);
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    @Operation(summary = "Client fetch offline messages")
    @RequestMapping(value = "fetchOfflineMsgs", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> fetchOfflineMsgs(@RequestBody OfflineMsgReqVO offlineMsgReqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();

        try {
            Optional<CIMServerResVO> cimServerResVO = accountService.loadRouteRelatedByUserId(offlineMsgReqVO.getReceiveUserId());

            cimServerResVO.ifPresent(cimServerRes -> {
                offlineMsgService.fetchOfflineMsgs(cimServerRes, offlineMsgReqVO.getReceiveUserId());
            });

            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());

        } catch (CIMException e) {
            res.setCode(e.getErrorCode());
            res.setMessage(e.getErrorMessage());
        }
        return res;
    }

    @Operation(summary = "私聊单条消息已读")
    @RequestMapping(value = "p2pMsgRead", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> p2pMsgRead(@RequestBody @Valid P2PMsgReadReqVO reqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();
        
        log.info("P2P message read: messageId={}, userId={}", reqVO.getMessageId(), reqVO.getUserId());
        
        try {
            readReceiptRouteService.handleP2PMsgRead(reqVO);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("Failed to handle p2p message read", e);
            res.setCode(StatusEnum.FAIL.getCode());
            res.setMessage(e.getMessage());
        }
        
        return res;
    }

    @Operation(summary = "会话级别已读（P2P或群聊）")
    @RequestMapping(value = "conversationRead", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> conversationRead(@RequestBody @Valid ConversationReadReqVO reqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();
        
        log.info("Conversation read: userId={}, conversationId={}, conversationType={}", 
                reqVO.getUserId(), reqVO.getConversationId(), reqVO.getConversationType());
        
        try {
            readReceiptRouteService.handleConversationRead(reqVO);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("Failed to handle conversation read", e);
            res.setCode(StatusEnum.FAIL.getCode());
            res.setMessage(e.getMessage());
        }
        
        return res;
    }

    @Operation(summary = "群聊消息已读")
    @RequestMapping(value = "groupMsgRead", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> groupMsgRead(@RequestBody @Valid GroupMsgReadReqVO reqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();
        
        log.info("Group message read: groupId={}, messageId={}, userId={}", 
                reqVO.getGroupId(), reqVO.getMessageId(), reqVO.getUserId());
        
        try {
            readReceiptRouteService.handleGroupMsgRead(reqVO);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("Failed to handle group message read", e);
            res.setCode(StatusEnum.FAIL.getCode());
            res.setMessage(e.getMessage());
        }
        
        return res;
    }

    @Operation(summary = "获取私聊会话未读消息数")
    @RequestMapping(value = "getP2PUnreadCount", method = RequestMethod.GET)
    @ResponseBody()
    public BaseResponse<UnreadCountResVO> getP2PUnreadCount(
            @RequestParam("userId") Long userId,
            @RequestParam("conversationId") String conversationId) {
        BaseResponse<UnreadCountResVO> res = new BaseResponse<>();
        
        try {
            UnreadCountResVO countRes = readReceiptRouteService.getP2PUnreadCount(userId, conversationId);
            res.setDataBody(countRes);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("Failed to get P2P unread count", e);
            res.setCode(StatusEnum.FAIL.getCode());
            res.setMessage(e.getMessage());
        }
        
        return res;
    }

    @Operation(summary = "获取群聊会话未读消息数")
    @RequestMapping(value = "getGroupUnreadCount", method = RequestMethod.GET)
    @ResponseBody()
    public BaseResponse<UnreadCountResVO> getGroupUnreadCount(
            @RequestParam("userId") Long userId,
            @RequestParam("groupId") Long groupId) {
        BaseResponse<UnreadCountResVO> res = new BaseResponse<>();
        
        try {
            UnreadCountResVO countRes = readReceiptRouteService.getGroupUnreadCount(userId, groupId);
            res.setDataBody(countRes);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("Failed to get group unread count", e);
            res.setCode(StatusEnum.FAIL.getCode());
            res.setMessage(e.getMessage());
        }
        
        return res;
    }

    @Operation(summary = "获取群聊单条消息已读人数")
    @RequestMapping(value = "getGroupMsgReadCount", method = RequestMethod.GET)
    @ResponseBody()
    public BaseResponse<GroupReadCountResVO> getGroupMsgReadCount(
            @RequestParam("groupId") Long groupId,
            @RequestParam("messageId") Long messageId) {
        BaseResponse<GroupReadCountResVO> res = new BaseResponse<>();
        
        try {
            GroupReadCountResVO countRes = readReceiptRouteService.getGroupMsgReadCount(groupId, messageId);
            res.setDataBody(countRes);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("Failed to get group message read count", e);
            res.setCode(StatusEnum.FAIL.getCode());
            res.setMessage(e.getMessage());
        }
        
        return res;
    }

    @Operation(summary = "批量获取群聊消息已读人数")
    @RequestMapping(value = "getGroupMsgsReadCount", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<List<GroupReadCountResVO>> getGroupMsgsReadCount(
            @RequestParam("groupId") Long groupId,
            @RequestBody List<Long> messageIds) {
        BaseResponse<List<GroupReadCountResVO>> res = new BaseResponse<>();
        
        try {
            List<GroupReadCountResVO> countRes = readReceiptRouteService.getGroupMsgsReadCount(groupId, messageIds);
            res.setDataBody(countRes);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("Failed to get group messages read count", e);
            res.setCode(StatusEnum.FAIL.getCode());
            res.setMessage(e.getMessage());
        }
        
        return res;
    }
}
