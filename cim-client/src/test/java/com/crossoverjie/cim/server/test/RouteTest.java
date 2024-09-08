package com.crossoverjie.cim.server.test;

import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 22:39
 * @since JDK 1.8
 */
//@SpringBootTest(classes = CIMClientApplication.class)
//@RunWith(SpringRunner.class)
@Slf4j
public class RouteTest {

    @Value("${cim.user.id}")
    private long userId;

    @Value("${cim.user.userName}")
    private String userName;

//    @Autowired
    private RouteRequest routeRequest ;

    // TODO: 2024/8/31 Integration test
//    @Test
    public void test() throws Exception {
        LoginReqVO vo = new LoginReqVO(userId,userName) ;
        com.crossoverjie.cim.route.api.vo.res.CIMServerResVO cimServer = routeRequest.getCIMServer(vo);
        log.info("cimServer=[{}]",cimServer.toString());
    }
}
