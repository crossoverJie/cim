package com.crossoverjie.cim.client.sdk.route;

import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.constant.Constant;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * @author zhongcanyu
 * @date 2025/6/5
 * @description
 */
public class OfflineMsgStoreRouteBaseTest extends AbstractRouteBaseTest {

    private MySQLContainer<?> mysql;

    @Override
    public void startRoute(String offlineModel) {
        redis.start();
        SpringApplication route = new SpringApplication(RouteApplication.class);
        String[] args;
        if (Constant.OfflineStoreMode.MYSQL.equals(offlineModel)) {
            mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
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

        route.setAdditionalProfiles("route");
        run = route.run(args);
    }

    @Override
    public void close() {
        if (mysql != null) {
            mysql.stop();
        }
        super.close();
    }
}
