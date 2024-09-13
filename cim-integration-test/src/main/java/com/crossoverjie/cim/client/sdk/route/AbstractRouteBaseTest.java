package com.crossoverjie.cim.client.sdk.route;

import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.client.sdk.server.AbstractServerBaseTest;
import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.RegisterInfoReqVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;
import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractRouteBaseTest extends AbstractServerBaseTest {

    @Container
    RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.4.0"));

    private ConfigurableApplicationContext run;
    public void startRoute() {
        redis.start();
        SpringApplication route = new SpringApplication(RouteApplication.class);
        String[] args = new String[]{
                "--spring.data.redis.host=" + redis.getHost(),
                "--spring.data.redis.port=" + redis.getMappedPort(6379),
                "--app.zk.addr=" + super.getZookeeperAddr(),
        };
        route.setAdditionalProfiles("route");
        run = route.run(args);
    }

    public void close(){
        super.close();
        redis.close();
        run.close();
    }

    public Long registerAccount(String userName) throws Exception {
        // register user
        RouteApi routeApi = com.crossoverjie.cim.route.util.SpringBeanFactory.getBean(RouteApi.class);
        RegisterInfoReqVO reqVO = new RegisterInfoReqVO();
        reqVO.setUserName(userName);
        BaseResponse<RegisterInfoResVO> account =
                routeApi.registerAccount(reqVO);
        return account.getDataBody().getUserId();
    }
}