package com.crossoverjie.cim.client.controller;

import com.crossoverjie.cim.client.client.CIMClient;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.GoogleProtocolVO;
import com.crossoverjie.cim.client.vo.req.GroupReqVO;
import com.crossoverjie.cim.client.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.client.vo.req.StringReqVO;
import com.crossoverjie.cim.client.vo.res.SendMsgResVO;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
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

    /**
     * 统计 service
     */
    @Autowired
    private CounterService counterService;

    @Autowired
    private CIMClient heartbeatClient ;



    @Autowired
    private RouteRequest routeRequest ;


    /**
     * 向服务端发消息 字符串
     * @param stringReqVO
     * @return
     */
    @ApiOperation("客户端发送消息，字符串")
    @RequestMapping(value = "sendStringMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<NULLBody> sendStringMsg(@RequestBody StringReqVO stringReqVO){
        BaseResponse<NULLBody> res = new BaseResponse();

        for (int i = 0; i < 100; i++) {
            heartbeatClient.sendStringMsg(stringReqVO.getMsg()) ;
        }

        // 利用 actuator 来自增
        counterService.increment(Constants.COUNTER_CLIENT_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }

    /**
     * 向服务端发消息 Google ProtoBuf
     * @param googleProtocolVO
     * @return
     */
    @ApiOperation("向服务端发消息 Google ProtoBuf")
    @RequestMapping(value = "sendProtoBufMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<NULLBody> sendProtoBufMsg(@RequestBody GoogleProtocolVO googleProtocolVO){
        BaseResponse<NULLBody> res = new BaseResponse();

        for (int i = 0; i < 100; i++) {
            heartbeatClient.sendGoogleProtocolMsg(googleProtocolVO) ;
        }

        // 利用 actuator 来自增
        counterService.increment(Constants.COUNTER_CLIENT_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }



    /**
     * 群发消息
     * @param sendMsgReqVO
     * @return
     */
    @ApiOperation("群发消息")
    @RequestMapping(value = "sendGroupMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse sendGroupMsg(@RequestBody SendMsgReqVO sendMsgReqVO) throws Exception {
        BaseResponse<NULLBody> res = new BaseResponse();

        GroupReqVO groupReqVO = new GroupReqVO(sendMsgReqVO.getUserId(),sendMsgReqVO.getMsg()) ;
        routeRequest.sendGroupMsg(groupReqVO) ;

        counterService.increment(Constants.COUNTER_SERVER_PUSH_COUNT);

        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }
}
