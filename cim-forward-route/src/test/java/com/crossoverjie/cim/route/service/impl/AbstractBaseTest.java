package com.crossoverjie.cim.route.service.impl;

import com.clevercloud.testcontainers.zookeeper.ZooKeeperContainer;
import com.redis.testcontainers.RedisContainer;
import java.time.Duration;
import java.util.List;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

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

    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("cim-test")
            .withUsername("cimUserName")
            .withPassword("cimPassWord")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("init.sql"),
                    "/docker-entrypoint-initdb.d/init.sql"
            )
            .withExposedPorts(3306)
            .withReuse(true);

    @BeforeAll
    public static void before(){
        redis.setExposedPorts(List.of(6379));
        redis.setPortBindings(List.of("6379:6379"));
        redis.start();

        zooKeeperContainer.setExposedPorts(List.of(2181));
        zooKeeperContainer.setPortBindings(List.of("2181:2181"));
        zooKeeperContainer.start();


        // 启动 MySQL
        mysql.start();

        // 动态设置 Spring 数据源配置（如果使用 Spring Boot）
        System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysql.getUsername());
        System.setProperty("spring.datasource.password", mysql.getPassword());
    }

    @AfterAll
    public static void after(){
        redis.stop();
        zooKeeperContainer.stop();
        mysql.stop();
    }

}
