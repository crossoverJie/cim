package com.crossoverjie.netty.action.client.controller;

import com.crossoverjie.netty.action.client.HeartbeatClient;
import com.crossoverjie.netty.action.client.vo.req.SendMsgReqVO;
import com.crossoverjie.netty.action.client.vo.res.SendMsgResVO;
import com.crossoverjie.netty.action.common.constant.Constants;
import com.crossoverjie.netty.action.common.enums.StatusEnum;
import com.crossoverjie.netty.action.common.pojo.CustomProtocol;
import com.crossoverjie.netty.action.common.res.BaseResponse;
import com.crossoverjie.netty.action.common.util.RandomUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /**
     * 统计 service
     */
    @Autowired
    private CounterService counterService;

    @Autowired
    private HeartbeatClient heartbeatClient ;

    /**
     * 向服务端发消息
     * @param sendMsgReqVO
     * @return
     */
    @ApiOperation("客户端发送消息")
    @RequestMapping("sendMsg")
    @ResponseBody
    public BaseResponse<SendMsgResVO> sendMsg(@RequestBody SendMsgReqVO sendMsgReqVO){
        BaseResponse<SendMsgResVO> res = new BaseResponse();
        heartbeatClient.sendMsg(new CustomProtocol(sendMsgReqVO.getId(),sendMsgReqVO.getMsg())) ;

        // 利用 actuator 来自增
        counterService.increment(Constants.COUNTER_CLIENT_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        res.setDataBody(sendMsgResVO) ;
        return res ;
    }
}
