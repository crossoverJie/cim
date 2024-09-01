package com.crossoverjie.netty;

import com.crossoverjie.cim.client.CIMClientApplication;
import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.util.SpringBeanFactory;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.server.CIMServerApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        System.out.println(MsgHandle.class);
        System.out.println(SpringBeanFactory.class);
        System.out.println(BaseResponse.class);
        System.out.println(RouteApplication.class);
        System.out.println(CIMServerApplication.class);
        System.out.println(CIMClientApplication.class);
        System.out.println("Hello world!");
    }
}