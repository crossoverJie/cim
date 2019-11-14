package com.crossoverjie.cim.route.controller;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.route.cache.ServerCache;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.ChatGroupService;
import com.crossoverjie.cim.route.service.MsgStoreService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.route.vo.req.*;
import com.crossoverjie.cim.route.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.vo.res.RegisterInfoResVO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 14:46
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/")
public class RouteController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

    @Autowired
    private ServerCache serverCache;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserInfoCacheService userInfoCacheService ;

    @Autowired
    private MsgStoreService msgStoreService;

    @Autowired
    private ChatGroupService chatGroupService;

    @Autowired
    private RouteHandle routeHandle ;

    @ApiOperation("群聊 API")
    @RequestMapping(value = "groupRoute", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> groupRoute(@RequestBody ChatReqVO groupReqVO) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        LOGGER.info("msg=[{}]", groupReqVO.toString());

        //获取所有的推送列表
        Map<Long, CIMServerResVO> serverResVOMap = accountService.loadRouteRelated();
        for (Map.Entry<Long, CIMServerResVO> cimServerResVOEntry : serverResVOMap.entrySet()) {
            Long userId = cimServerResVOEntry.getKey();
            CIMServerResVO value = cimServerResVOEntry.getValue();
            if (userId.equals(groupReqVO.getUserId())){
                //过滤掉自己
                CIMUserInfo cimUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());
                LOGGER.warn("过滤掉了发送者 userId={}",cimUserInfo.toString());
                continue;
            }

            //推送消息
            String url = "http://" + value.getIp() + ":" + value.getHttpPort() + "/sendMsg" ;
            ChatReqVO chatVO = new ChatReqVO(userId,groupReqVO.getMsg()) ;

            accountService.pushMsg(url,groupReqVO.getUserId(),chatVO);

        }

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    @ApiOperation("创建群组 API")
    @RequestMapping(value = "createChatGroup", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse createChatGroup(@RequestBody CreateGroupReqVo createGroupReqVo) throws Exception {
        if (createGroupReqVo.getUserId() == null || createGroupReqVo.getChatGroupName() == null || createGroupReqVo.getChatGroupName().isEmpty())
            return BaseResponse.create(NULLBody.create(), StatusEnum.FAIL);

        String[] members = createGroupReqVo.getMembers().split(",");
        List<Long> memberIds = new LinkedList<>();
        for (String id : members) {
            memberIds.add(Long.valueOf(id));
        }
        Long chatGroupId = chatGroupService.createChatGroup(createGroupReqVo.getChatGroupName(), createGroupReqVo.getUserId(), memberIds);
        if (chatGroupId == null || chatGroupId <= 0)
            return BaseResponse.create(NULLBody.create(), StatusEnum.FAIL);

        return BaseResponse.create(String.valueOf(chatGroupId), StatusEnum.SUCCESS);
    }

    @ApiOperation("指定群发送聊天 API")
    @RequestMapping(value = "groupChatRoute", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse groupChatRoute(@RequestBody ChatReqVO chatReqVO) throws Exception {
        if (chatReqVO.getUserId() == null || chatReqVO.getMsg() == null || chatReqVO.getMsg().isEmpty())
            return BaseResponse.create(null, StatusEnum.FAIL);

        boolean isExist = chatGroupService.isChatGroupExist(chatReqVO.getUserId());
        if (!isExist)
            return BaseResponse.create(null, StatusEnum.CHAT_GROUP_NO_EXIST);

        Integer receivedCount = chatGroupService.sendGroupMessage(chatReqVO.getUserId(), chatReqVO.getMsg());

        return BaseResponse.create("收到消息人数:" + receivedCount, StatusEnum.SUCCESS);
    }

    @ApiOperation("群组加入成员 API")
    @RequestMapping(value = "addGroupMemberRoute", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse addGroupMemberRoute(@RequestBody AddGroupMemberReqVo addGroupMemberReqVo) throws Exception {
        if (addGroupMemberReqVo.getUserId() == null || addGroupMemberReqVo.getChatGroupId() == null)
            return BaseResponse.create(null, StatusEnum.FAIL);

        boolean isExist = chatGroupService.isChatGroupExist(addGroupMemberReqVo.getChatGroupId());
        if (!isExist)
            return BaseResponse.create(null, StatusEnum.CHAT_GROUP_NO_EXIST);

        boolean success = chatGroupService.addGroupMember(addGroupMemberReqVo.getChatGroupId(), addGroupMemberReqVo.getUserId());
        if (!success)
            return BaseResponse.create(null, StatusEnum.FAIL);

        return BaseResponse.create(null, StatusEnum.SUCCESS);
    }


    /**
     * 私聊路由
     *
     * @param p2pRequest
     * @return
     */
    @ApiOperation("私聊 API")
    @RequestMapping(value = "p2pRoute", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> p2pRoute(@RequestBody P2PReqVO p2pRequest) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        try {
            //获取接收消息用户的路由信息
            CIMServerResVO cimServerResVO = accountService.loadRouteRelatedByUserId(p2pRequest.getReceiveUserId());
            //推送消息
            String url = "http://" + cimServerResVO.getIp() + ":" + cimServerResVO.getHttpPort() + "/sendMsg" ;

            //p2pRequest.getReceiveUserId()==>消息接收者的 userID
            ChatReqVO chatVO = new ChatReqVO(p2pRequest.getReceiveUserId(),p2pRequest.getMsg()) ;
            accountService.pushMsg(url,p2pRequest.getUserId(),chatVO);

            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());

        }catch (CIMException e){
            res.setCode(e.getErrorCode());
            res.setMessage(e.getErrorMessage());
        }
        return res;
    }

    /**
     * 私聊路由,支持离线
     *
     * @param p2pRequest
     * @return
     */
    @ApiOperation("私聊,支持离线 API")
    @RequestMapping(value = "p2pRouteStore", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> p2pRouteStore (@RequestBody P2PReqVO p2pRequest) throws Exception {
        String msgUUID = UUID.randomUUID().toString();
        //先存一份消息详情
        boolean putMsgSuccess = msgStoreService.putMessage(msgUUID,p2pRequest.getUserId(),p2pRequest.getReceiveUserId(),p2pRequest.getMsg());
        if (!putMsgSuccess) {
            return BaseResponse.create(null,StatusEnum.FAIL);
        }

        BaseResponse<NULLBody> returnResponse = new BaseResponse();

        try {
            //尝试获取接收消息用户的路由信息
            CIMServerResVO cimServerResVO = accountService.loadRouteRelatedByUserId(p2pRequest.getReceiveUserId());
            //推送消息
            String url = "http://" + cimServerResVO.getIp() + ":" + cimServerResVO.getHttpPort() + "/sendMsg" ;
            ChatReqVO chatVO = new ChatReqVO(p2pRequest.getReceiveUserId(),p2pRequest.getMsg()) ;
            returnResponse = accountService.pushMsg(url,p2pRequest.getUserId(),chatVO);
        }catch (Exception e){
        }

        if (returnResponse == null || !returnResponse.isSuccess()) {
            //用户不在线或发送失败，记录离线消息
            boolean addUserOffLineMessage = msgStoreService.addUserOffLineMessage(p2pRequest.getReceiveUserId(),msgUUID);
            if (!addUserOffLineMessage) {
                return BaseResponse.create(null,StatusEnum.FAIL);
            }
            return BaseResponse.create(null,StatusEnum.SUCCESS_OFFLINE);
        }

        return BaseResponse.create(null,StatusEnum.SUCCESS);
    }

    @ApiOperation("客户端下线")
    @RequestMapping(value = "offLine", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> offLine(@RequestBody ChatReqVO groupReqVO) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        CIMUserInfo cimUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());

        LOGGER.info("下线用户[{}]", cimUserInfo.toString());
        accountService.offLine(groupReqVO.getUserId());

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    /**
     * 获取一台 CIM server
     *
     * @return
     */
    @ApiOperation("登录并获取服务器")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<CIMServerResVO> login(@RequestBody LoginReqVO loginReqVO) throws Exception {
        BaseResponse<CIMServerResVO> res = new BaseResponse();

        //登录校验
        StatusEnum status = accountService.login(loginReqVO);
        if (status == StatusEnum.SUCCESS) {

            String server = routeHandle.routeServer(serverCache.getAll(),String.valueOf(loginReqVO.getUserId()));
            String[] serverInfo = server.split(":");
            CIMServerResVO vo = new CIMServerResVO(serverInfo[0], Integer.parseInt(serverInfo[1]),Integer.parseInt(serverInfo[2]));

            //保存路由信息
            accountService.saveRouteInfo(loginReqVO,server);

            res.setDataBody(vo);

        }
        res.setCode(status.getCode());
        res.setMessage(status.getMessage());

        return res;
    }

    /**
     * 注册账号
     *
     * @return
     */
    @ApiOperation("注册账号")
    @RequestMapping(value = "registerAccount", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<RegisterInfoResVO> registerAccount(@RequestBody RegisterInfoReqVO registerInfoReqVO) throws Exception {
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
    @ApiOperation("获取所有在线用户")
    @RequestMapping(value = "onlineUser", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<Set<CIMUserInfo>> onlineUser() throws Exception {
        BaseResponse<Set<CIMUserInfo>> res = new BaseResponse();

        Set<CIMUserInfo> cimUserInfos = userInfoCacheService.onlineUser();
        res.setDataBody(cimUserInfos) ;
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }





}
