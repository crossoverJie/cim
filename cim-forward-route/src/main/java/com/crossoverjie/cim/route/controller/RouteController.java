package com.crossoverjie.cim.route.controller;

import com.crossoverjie.cim.route.vo.req.P2PRequest;
import com.crossoverjie.cim.route.vo.req.GroupRequest;
import com.crossoverjie.netty.action.common.enums.StatusEnum;
import com.crossoverjie.netty.action.common.res.BaseResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @ApiOperation("群聊 API")
    @RequestMapping(value = "groupRoute",method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse groupRoute(@RequestBody GroupRequest groupRequest){
        BaseResponse res = new BaseResponse();

        LOGGER.info("msg=[{}]",groupRequest.toString());

        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }


    /**
     * 私聊路由
     * @param p2pRequest
     * @return
     */
    @ApiOperation("私聊 API")
    @RequestMapping(value = "p2pRoute",method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse p2pRoute(@RequestBody P2PRequest p2pRequest){
        BaseResponse res = new BaseResponse();

        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }


}
