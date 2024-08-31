package com.crossoverjie.cim.client;

import com.clevercloud.testcontainers.zookeeper.ZooKeeperContainer;
import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.util.SpringBeanFactory;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.RegisterInfoReqVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;
import com.crossoverjie.cim.server.CIMServerApplication;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.redis.testcontainers.RedisContainer;
import jakarta.annotation.Resource;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class ClientTest {
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName
            .parse("zookeeper")
            .withTag("3.9.2");

    private static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofSeconds(60);

    @Container
    public final ZooKeeperContainer container = new ZooKeeperContainer(DEFAULT_IMAGE_NAME, DEFAULT_STARTUP_TIMEOUT);

    @Container
    RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.4.0"));

    @Test
    public void olu() throws Exception {
        container.start();
        redis.start();
        String host = container.getHost();
        Integer mappedPort = container.getMappedPort(ZooKeeperContainer.DEFAULT_CLIENT_PORT);
        String zkAddr = host + ":" + mappedPort;
        SpringApplication server = new SpringApplication(CIMServerApplication.class);
        server.run("--app.zk.addr=" + zkAddr);

        SpringApplication route = new SpringApplication(RouteApplication.class);
        String[] args = new String[]{
                "--spring.data.redis.host=" + redis.getHost(),
                "--spring.data.redis.port=" + redis.getMappedPort(6379),
                "--app.zk.addr=" + zkAddr,
        };
        route.setAdditionalProfiles("route");
        route.run(args);
        // register user
        String userName = "crossoverJie";
        RouteApi routeApi = com.crossoverjie.cim.route.util.SpringBeanFactory.getBean(RouteApi.class);
        BaseResponse<RegisterInfoResVO> account =
                routeApi.registerAccount(RegisterInfoReqVO.builder()
                        .userName(userName)
                        .build());


        SpringApplication client = new SpringApplication(CIMClientApplication.class);
        client.setAdditionalProfiles("client");
        args = new String[]{
                "--cim.user.id=" + account.getDataBody().getUserId(),
                "--cim.user.userName=" + userName
        };
        client.run(args);
        MsgHandle msgHandle = SpringBeanFactory.getBean(MsgHandle.class);
        msgHandle.innerCommand(":olu");

    }

}
