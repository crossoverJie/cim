package com.crossoverjie.cim.route.service.impl;

import com.clevercloud.testcontainers.zookeeper.ZooKeeperContainer;
import com.redis.testcontainers.RedisContainer;
import java.time.Duration;
import java.util.List;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class AbstractBaseTest {

    @Container
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7.4.0"));

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName
            .parse("zookeeper")
            .withTag("3.9.2");

    private static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofSeconds(60);

    @Container
    static final ZooKeeperContainer
            zooKeeperContainer = new ZooKeeperContainer(DEFAULT_IMAGE_NAME, DEFAULT_STARTUP_TIMEOUT);


    @BeforeAll
    public static void before(){
        redis.setExposedPorts(List.of(6379));
        redis.setPortBindings(List.of("6379:6379"));
        redis.start();

        zooKeeperContainer.setExposedPorts(List.of(2181));
        zooKeeperContainer.setPortBindings(List.of("2181:2181"));
        zooKeeperContainer.start();
    }

    @AfterAll
    public static void after(){
        redis.stop();
        zooKeeperContainer.stop();
    }

}
