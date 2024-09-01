package com.crossoverjie.cim.server;

import com.clevercloud.testcontainers.zookeeper.ZooKeeperContainer;
import java.time.Duration;
import lombok.Getter;
import org.junit.Before;
import org.springframework.boot.SpringApplication;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractServerBaseTest {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName
            .parse("zookeeper")
            .withTag("3.9.2");

    private static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofSeconds(60);

    @Container
    public final ZooKeeperContainer
            zooKeeperContainer = new ZooKeeperContainer(DEFAULT_IMAGE_NAME, DEFAULT_STARTUP_TIMEOUT);

    @Getter
    private String zookeeperAddr;

    public void startServer() {
        zooKeeperContainer.start();
        zookeeperAddr = String.format("%s:%d", zooKeeperContainer.getHost(), zooKeeperContainer.getMappedPort(ZooKeeperContainer.DEFAULT_CLIENT_PORT));
        SpringApplication server = new SpringApplication(CIMServerApplication.class);
        server.run("--app.zk.addr=" + zookeeperAddr);
    }

}
