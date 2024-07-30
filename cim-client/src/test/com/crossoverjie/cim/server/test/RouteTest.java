package com.crossoverjie.cim.server.test;

import com.crossoverjie.cim.client.CIMClientApplication;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.LoginReqVO;
import com.crossoverjie.cim.client.vo.res.CIMServerResVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 22:39
 * @since JDK 1.8
 */
@SpringBootTest(classes = CIMClientApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class RouteTest {

    @Value("${cim.user.id}")
    private long userId;

    @Value("${cim.user.userName}")
    private String userName;

    @Autowired
    private RouteRequest routeRequest ;

    @Test
    public void test() throws Exception {
        LoginReqVO vo = new LoginReqVO(userId,userName) ;
        CIMServerResVO.ServerInfo cimServer = routeRequest.getCIMServer(vo);
        log.info("cimServer=[{}]",cimServer.toString());
    }
}
