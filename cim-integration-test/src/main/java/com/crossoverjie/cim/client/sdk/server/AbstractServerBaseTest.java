package com.crossoverjie.cim.client.sdk.server;

import com.clevercloud.testcontainers.zookeeper.ZooKeeperContainer;
import com.crossoverjie.cim.server.CIMServerApplication;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
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

    private ConfigurableApplicationContext singleRun;
    public void starSingleServer() {
        zooKeeperContainer.start();
        zookeeperAddr = String.format("%s:%d", zooKeeperContainer.getHost(), zooKeeperContainer.getMappedPort(ZooKeeperContainer.DEFAULT_CLIENT_PORT));
        SpringApplication server = new SpringApplication(CIMServerApplication.class);
        singleRun = server.run("--app.zk.addr=" + zookeeperAddr);
    }
    public void stopSingle(){
        singleRun.close();
    }

    private final Map<Integer, ConfigurableApplicationContext> runMap = new HashMap<>(2);
    public void startTwoServer() {
        if (!zooKeeperContainer.isRunning()){
            zooKeeperContainer.start();
        }
        zookeeperAddr = String.format("%s:%d", zooKeeperContainer.getHost(), zooKeeperContainer.getMappedPort(ZooKeeperContainer.DEFAULT_CLIENT_PORT));
        SpringApplication server = new SpringApplication(CIMServerApplication.class);
        String[] args1 = new String[]{
                "--cim.server.port=11211",
                "--server.port=8081",
                "--app.zk.addr=" + zookeeperAddr,
        };
        ConfigurableApplicationContext run1 = server.run(args1);
        runMap.put(Integer.parseInt("11211"), run1);


        SpringApplication server2 = new SpringApplication(CIMServerApplication.class);
        String[] args2 = new String[]{
                "--cim.server.port=11212",
                "--server.port=8082",
                "--app.zk.addr=" + zookeeperAddr,
        };
        ConfigurableApplicationContext run2 = server2.run(args2);
        runMap.put(Integer.parseInt("11212"), run2);
    }

    public void stopServer(Integer port) {
        runMap.get(port).close();
        runMap.remove(port);
    }
    public void stopTwoServer() {
        runMap.forEach((k,v) -> v.close());
    }

    public void close(){
        zooKeeperContainer.close();
    }

}
