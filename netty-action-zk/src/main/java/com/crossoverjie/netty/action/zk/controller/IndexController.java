package com.crossoverjie.netty.action.zk.controller;

import com.crossoverjie.netty.action.common.enums.StatusEnum;
import com.crossoverjie.netty.action.common.res.BaseResponse;
import com.crossoverjie.netty.action.common.res.NULLBody;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
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
     * 获取所有路由节点
     * @return
     */
    @ApiOperation("获取所有路由节点")
    @RequestMapping(value = "getRoute",method = RequestMethod.POST)
    @ResponseBody()
    public BaseResponse<NULLBody> getRoute(){
        BaseResponse<NULLBody> res = new BaseResponse();
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage("127.0.0.1:8080") ;
        return res ;
    }


}
