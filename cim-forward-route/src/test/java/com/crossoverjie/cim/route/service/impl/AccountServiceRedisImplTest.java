package com.crossoverjie.cim.route.service.impl;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.service.AccountService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = RouteApplication.class)
public class AccountServiceRedisImplTest extends AbstractBaseTest{

    @Autowired
    private AccountService accountService ;

    @Test
    public void loadRouteRelated() throws Exception {
        for (int i = 0; i < 100; i++) {

            Map<Long, CIMServerResVO> longCIMServerResVOMap = accountService.loadRouteRelated();
            log.info("longCIMServerResVOMap={},cun={}" , JSON.toJSONString(longCIMServerResVOMap),i);
        }
    }

}