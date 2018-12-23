package com.crossoverjie.cim.route.service.impl;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.vo.res.CIMServerResVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest(classes = RouteApplication.class)
@RunWith(SpringRunner.class)
public class AccountServiceRedisImplTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(AccountServiceRedisImplTest.class);

    @Autowired
    private AccountService accountService ;

    @Test
    public void loadRouteRelated() throws Exception {
        Map<Long, CIMServerResVO> longCIMServerResVOMap = accountService.loadRouteRelated();
        LOGGER.info("longCIMServerResVOMap={}" , JSON.toJSONString(longCIMServerResVOMap));
    }

}