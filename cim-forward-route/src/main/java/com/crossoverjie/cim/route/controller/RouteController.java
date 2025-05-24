package com.crossoverjie.cim.route.controller;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.*;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.CommonBizService;
import com.crossoverjie.cim.route.service.OfflineMsgPushService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.server.api.ServerApi;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private OfflineMsgPushService offlineMsgPushService;


    @Operation(summary = "群聊 API")
    @RequestMapping(value = "groupRoute", method = RequestMethod.POST)
    @ResponseBody()
    @Override
    public BaseResponse<NULLBody> groupRoute(@RequestBody ChatReqVO groupReqVO) {
        BaseResponse<NULLBody> res = new BaseResponse();

        log.info("msg=[{}]", groupReqVO.toString());

        Map<Long, CIMServerResVO> serverResVoMap = accountService.loadRouteRelated();
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
            accountService.pushMsg(cimServerResVO, groupReqVO.getUserId(), chatVO);

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
                offlineMsgPushService.saveOfflineMsg(p2pRequest);
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
            //获取接收消息用户的路由信息
            Optional<CIMServerResVO> cimServerResVO = accountService.loadRouteRelatedByUserId(offlineMsgReqVO.getReceiveUserId());

            cimServerResVO.ifPresent(cimServerRes -> {
                offlineMsgPushService.fetchOfflineMsgs(cimServerRes, offlineMsgReqVO.getReceiveUserId());
            });

            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());

        } catch (CIMException e) {
            res.setCode(e.getErrorCode());
            res.setMessage(e.getErrorMessage());
        }
        return res;
    }
}
