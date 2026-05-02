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
import com.crossoverjie.cim.common.util.SnowflakeIdWorker;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.req.MsgReadAckReqVO;
import com.crossoverjie.cim.route.api.vo.req.OfflineMsgReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.req.RegisterInfoReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.MsgReadStatusResVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.CommonBizService;
import com.crossoverjie.cim.route.service.MsgMetadataService;
import com.crossoverjie.cim.route.service.MsgReadService;
import com.crossoverjie.cim.route.service.OfflineMsgService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.server.api.ServerApi;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
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
import org.springframework.web.bind.annotation.ResponseBody;

import static com.crossoverjie.cim.common.enums.StatusEnum.OFF_LINE;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 22/05/2018 14:46
 * @since JDK 1.8
 */
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
    private SnowflakeIdWorker snowflakeIdWorker;

    @Resource
    private MsgMetadataService msgMetadataService;

    @Resource
    private MsgReadService msgReadService;


    @Operation(summary = "群聊 API")
    @RequestMapping(value = "groupRoute", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> groupRoute(@RequestBody ChatReqVO groupReqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();

        log.info("msg=[{}]", groupReqVO.toString());

        Map<Long, CIMServerResVO> serverResVoMap = accountService.loadRouteRelated();
        
        long msgId = snowflakeIdWorker.nextId();
        int groupMemberCount = serverResVoMap.size() - 1;
        
        msgMetadataService.saveMsgSender(msgId, groupReqVO.getUserId());
        
        Map<String, String> extraProperties = new HashMap<>();
        extraProperties.put(Constants.MetaKey.MSG_ID, String.valueOf(msgId));
        extraProperties.put(Constants.MetaKey.GROUP_MEMBER_COUNT, String.valueOf(groupMemberCount));
        
        for (Map.Entry<Long, CIMServerResVO> cimServerResVoEntry : serverResVoMap.entrySet()) {
            Long userId = cimServerResVoEntry.getKey();
            CIMServerResVO cimServerResVO = cimServerResVoEntry.getValue();
            if (userId.equals(groupReqVO.getUserId())) {
                // Skip the sender
                Optional<CIMUserInfo> cimUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());
                cimUserInfo.ifPresent(userInfo -> log.warn("skip send user userId={}", userInfo));
                continue;
            }

            // Push message
            ChatReqVO chatVO = new ChatReqVO(userId, groupReqVO.getMsg(), null);
            accountService.pushMsg(cimServerResVO, groupReqVO.getUserId(), chatVO, extraProperties);

        }

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }


    /**
     * 私聊路由
     *
     * @param p2pRequest
     * @return
     */
    @Operation(summary = "私聊 API")
    @RequestMapping(value = "p2pRoute", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> p2pRoute(@RequestBody P2PReqVO p2pRequest) {
        BaseResponse<NULLBody> res = new BaseResponse();

        try {
            //获取接收消息用户的路由信息
            Optional<CIMServerResVO> cimServerResVO = accountService.loadRouteRelatedByUserId(p2pRequest.getReceiveUserId());
            if (cimServerResVO.isEmpty()) {
                log.warn("userId={} not online, save offline msg", p2pRequest.getReceiveUserId());
                offlineMsgService.saveOfflineMsg(p2pRequest);
                throw new CIMException(OFF_LINE);
            }

            //p2pRequest.getReceiveUserId()==>消息接收者的 userID
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

    /**
     * 获取一台 CIM server
     *
     * @return
     */
    @Operation(summary = "登录并获取服务器")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<CIMServerResVO> login(@RequestBody LoginReqVO loginReqVO) throws Exception {
        BaseResponse<CIMServerResVO> res = new BaseResponse();
        //登录校验
        StatusEnum status = accountService.login(loginReqVO);
        res.setCode(status.getCode());
        res.setMessage(status.getMessage());
        if (status != StatusEnum.SUCCESS) {
            return res;
        }

        // check server available
        Set<String> availableServerList = metaStore.getAvailableServerList();
        String key = String.valueOf(loginReqVO.getUserId());
        String server =
                routeHandle.routeServer(List.copyOf(availableServerList), key);
        log.info("userInfo=[{}] route server info=[{}]", loginReqVO, server);

        RouteInfo routeInfo = RouteInfoParseUtil.parse(server);
        routeInfo = commonBizService.checkServerAvailable(routeInfo, key);

        //保存路由信息
        accountService.saveRouteInfo(loginReqVO, server);

        CIMServerResVO vo =
                new CIMServerResVO(routeInfo.getIp(), routeInfo.getCimServerPort(), routeInfo.getHttpPort());
        res.setDataBody(vo);
        return res;
    }

    /**
     * 注册账号
     *
     * @return
     */
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

    /**
     * 获取所有在线用户
     *
     * @return
     */
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
            // Get the routing information of the user receiving the message
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

    @Operation(summary = "消息已读回执")
    @RequestMapping(value = "msgReadAck", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> msgReadAck(@RequestBody MsgReadAckReqVO msgReadAckReqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();

        log.info("msgReadAck: msgId={}, userId={}", msgReadAckReqVO.getMsgId(), msgReadAckReqVO.getUserId());

        Long sendUserId = msgMetadataService.getMsgSender(msgReadAckReqVO.getMsgId());
        if (sendUserId == null) {
            log.warn("Message sender not found for msgId={}", msgReadAckReqVO.getMsgId());
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
            return res;
        }

        Optional<CIMUserInfo> readUserInfo = userInfoCacheService.loadUserInfoByUserId(msgReadAckReqVO.getUserId());
        String readUserName = readUserInfo.map(CIMUserInfo::getUserName).orElse(String.valueOf(msgReadAckReqVO.getUserId()));

        msgReadService.markAsRead(msgReadAckReqVO.getMsgId(), msgReadAckReqVO.getUserId(), readUserName);

        if (!sendUserId.equals(msgReadAckReqVO.getUserId())) {
            Optional<CIMServerResVO> sendUserServer = accountService.loadRouteRelatedByUserId(sendUserId);
            sendUserServer.ifPresent(cimServerResVO -> {
                Optional<CIMUserInfo> sendUserInfo = userInfoCacheService.loadUserInfoByUserId(sendUserId);
                sendUserInfo.ifPresent(sendInfo -> {
                    String url = "http://" + cimServerResVO.getIp() + ":" + cimServerResVO.getHttpPort();
                    
                    Map<String, String> properties = new HashMap<>();
                    properties.put(Constants.MetaKey.MSG_ID, String.valueOf(msgReadAckReqVO.getMsgId()));
                    properties.put(Constants.MetaKey.READ_USER_ID, String.valueOf(msgReadAckReqVO.getUserId()));
                    properties.put(Constants.MetaKey.READ_USER_NAME, readUserName);
                    properties.put(Constants.MetaKey.READ_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

                    try {
                        com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO vo =
                                new com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO(
                                        null, 
                                        sendUserId, 
                                        null, 
                                        com.crossoverjie.cim.common.protocol.BaseCommand.MESSAGE_READ_STATUS
                                );
                        vo.setProperties(properties);
                        serverApi.sendMsg(vo, url);
                        log.info("Pushed read status to sender: msgId={}, sendUserId={}, readUserId={}",
                                msgReadAckReqVO.getMsgId(), sendUserId, msgReadAckReqVO.getUserId());
                    } catch (Exception e) {
                        log.error("Failed to push read status to sender", e);
                    }
                });
            });
        }

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    @Operation(summary = "查询消息已读状态")
    @RequestMapping(value = "getMsgReadStatus", method = RequestMethod.GET)
    @ResponseBody()
    @Override
    public BaseResponse<MsgReadStatusResVO> getMsgReadStatus(Long msgId) {
        BaseResponse<MsgReadStatusResVO> res = new BaseResponse<>();

        log.info("getMsgReadStatus: msgId={}", msgId);

        Long sendUserId = msgMetadataService.getMsgSender(msgId);
        if (sendUserId == null) {
            log.warn("Message not found for msgId={}", msgId);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
            res.setDataBody(MsgReadStatusResVO.builder()
                    .msgId(msgId)
                    .readCount(0L)
                    .readUsers(new java.util.HashMap<>())
                    .build());
            return res;
        }

        Map<Long, String> readUsers = msgReadService.getReadUsers(msgId);
        long readCount = msgReadService.getReadCount(msgId);

        MsgReadStatusResVO statusResVO = MsgReadStatusResVO.builder()
                .msgId(msgId)
                .readCount(readCount)
                .readUsers(readUsers)
                .build();

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        res.setDataBody(statusResVO);

        log.info("getMsgReadStatus result: msgId={}, readCount={}", msgId, readCount);
        return res;
    }
}
