package com.crossoverjie.cim.server.controller;

import com.crossoverjie.cim.common.core.proxy.DynamicUrl;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.server.api.ServerApi;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.api.vo.res.SendMsgResVO;
import com.crossoverjie.cim.server.server.CIMServer;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IndexController implements ServerApi {

    @Autowired
    private CIMServer cimServer;


    /**
     *
     * @param sendMsgReqVO
     * @return
     */
    @Override
    @Operation(summary = "Push msg to client")
    @RequestMapping(value = "sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<SendMsgResVO> sendMsg(@RequestBody SendMsgReqVO sendMsgReqVO, @DynamicUrl String url){
        BaseResponse<SendMsgResVO> res = new BaseResponse();
        cimServer.sendMsg(sendMsgReqVO) ;

        // TODO: 2024/5/30 metrics

        SendMsgResVO sendMsgResVO = new SendMsgResVO() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        res.setDataBody(sendMsgResVO) ;
        return res ;
    }

}
