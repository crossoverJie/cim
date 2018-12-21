package com.crossoverjie.netty.action.controller;

import com.crossoverjie.netty.action.common.constant.Constants;
import com.crossoverjie.netty.action.common.enums.StatusEnum;
import com.crossoverjie.netty.action.common.res.BaseResponse;
import com.crossoverjie.netty.action.server.HeartBeatServer;
import com.crossoverjie.netty.action.vo.req.SendMsgReqVO;
import com.crossoverjie.netty.action.vo.res.SendMsgResVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
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
public class IndexController {

    @Autowired
    private HeartBeatServer heartbeatClient ;


    /**
     * 统计 service
     */
    @Autowired
    private CounterService counterService;

    /**
     * 向服务端发消息
     * @param sendMsgReqVO
     * @return
     */
    @ApiOperation("服务端发送消息")
    @RequestMapping(value = "sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<SendMsgResVO> sendMsg(@RequestBody SendMsgReqVO sendMsgReqVO){
        BaseResponse<SendMsgResVO> res = new BaseResponse();
        heartbeatClient.sendGoogleProtoMsg(sendMsgReqVO) ;

        counterService.increment(Constants.COUNTER_SERVER_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        res.setDataBody(sendMsgResVO) ;
        return res ;
    }

}
