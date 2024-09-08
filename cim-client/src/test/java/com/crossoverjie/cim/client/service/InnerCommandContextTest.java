package com.crossoverjie.cim.client.service;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.client.CIMClientApplication;
import com.crossoverjie.cim.common.core.proxy.RpcProxyManager;
import com.crossoverjie.cim.common.enums.SystemCommandEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = CIMClientApplication.class)
@RunWith(SpringRunner.class)
public class InnerCommandContextTest {

    @Autowired
    private InnerCommandContext context;

    @Test
    public void execute() throws Exception {
        String msg = ":all";
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }

//    @Test
    public void execute3() throws Exception {
        // TODO: 2024/8/31 Integration test
        String msg = SystemCommandEnum.ONLINE_USER.getCommandType();
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }

    @Test
    public void execute4() throws Exception {
        String msg = ":q 天气";
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }

    @Test
    public void execute5() throws Exception {
        String msg = ":q crossoverJie";
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }

    @Test
    public void execute6() throws Exception {
        String msg = SystemCommandEnum.AI.getCommandType();
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }

    @Test
    public void execute7() throws Exception {
        String msg = SystemCommandEnum.QAI.getCommandType();
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }

//    @Test
    public void execute8() throws Exception {
        // TODO: 2024/8/31 Integration test
        String msg = ":pu cross";
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }

    @Test
    public void execute9() throws Exception {
        String msg = SystemCommandEnum.INFO.getCommandType();
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }

    @Test
    public void execute10() throws Exception {
        String msg = "dsds";
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }



   // @Test
    public void quit() throws Exception {
        String msg = ":q!";
        InnerCommand execute = context.getInstance(msg);
        execute.process(msg) ;
    }
}