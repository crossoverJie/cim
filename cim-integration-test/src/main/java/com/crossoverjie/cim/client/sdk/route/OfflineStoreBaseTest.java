package com.crossoverjie.cim.client.sdk.route;

import com.crossoverjie.cim.client.sdk.server.AbstractServerBaseTest;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.RegisterInfoReqVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;
import com.crossoverjie.cim.route.constant.Constant;
import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * @author zhongcanyu
 * @date 2025/6/5
 * @description
 */
public abstract class OfflineStoreBaseTest extends AbstractServerBaseTest {

    @Container
    RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.4.0"));

    private MySQLContainer<?> mysql;

    private ConfigurableApplicationContext run;

    public void startRoute(String offlineModel) {
        String[] args;
        if(Constant.OfflineStoreMode.MYSQL.equals(offlineModel)){
            mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.31"))
                    .withDatabaseName("cim-test")
                    .withUsername("cimUserName")
                    .withPassword("cimPassWord")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("init.sql"),
                            "/docker-entrypoint-initdb.d/init.sql"
                    )
                    .withExposedPorts(3306)
                    .withReuse(true);
            mysql.start();

            args = new String[]{
                    "--spring.data.redis.host=" + redis.getHost(),
                    "--spring.data.redis.port=" + redis.getMappedPort(6379),
                    "--app.zk.addr=" + super.getZookeeperAddr(),
                    "--offline.store.model=" + offlineModel,
                    "--spring.datasource.url=" + mysql.getJdbcUrl(),
                    "--spring.datasource.username=" + mysql.getUsername(),
                    "--spring.datasource.password=" + mysql.getPassword()
            };
        } else {
            args = new String[]{
                    "--spring.data.redis.host=" + redis.getHost(),
                    "--spring.data.redis.port=" + redis.getMappedPort(6379),
                    "--app.zk.addr=" + super.getZookeeperAddr(),
                    "--offline.store.model=" + offlineModel,
            };
        }

        redis.start();
        SpringApplication route = new SpringApplication(RouteApplication.class);
        route.setAdditionalProfiles("route");
        run = route.run(args);
    }

    public void close(){
        if (mysql != null) {
            mysql.stop();
        }
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
