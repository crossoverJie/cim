package com.crossoverjie.cim.route.controller;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.route.cache.ServerCache;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.route.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.vo.req.RegisterInfoReqVO;
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

import java.util.Map;

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
     * 私聊路由
     *
     * @param p2pRequest
     * @return
     */
    @ApiOperation("私聊 API")
    @RequestMapping(value = "p2pRoute", method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> p2pRoute(@RequestBody P2PReqVO p2pRequest) {
        BaseResponse<NULLBody> res = new BaseResponse();

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
        boolean login = accountService.login(loginReqVO);
        if (login) {
            String server = serverCache.selectServer();
            String[] serverInfo = server.split(":");
            CIMServerResVO vo = new CIMServerResVO(serverInfo[0], Integer.parseInt(serverInfo[1]),Integer.parseInt(serverInfo[2]));

            //保存路由信息
            accountService.saveRouteInfo(loginReqVO,server);

            res.setDataBody(vo);
            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());
        } else {
            res.setCode(StatusEnum.REPEAT_LOGIN.getCode());
            res.setMessage(StatusEnum.REPEAT_LOGIN.getMessage());
        }

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


}
