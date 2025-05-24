package com.crossoverjie.cim.route;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author crossoverJie
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
@MapperScan("com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper")
@ComponentScan("com.crossoverjie.cim")
public class RouteApplication implements CommandLineRunner{

	public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
		log.info("Start cim route success!!!");
	}

	@Override
	public void run(String... args) throws Exception {
	}
}