package com.crossoverjie.cim.route;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.crossoverjie.cim.persistence")
public class RouteApplication implements CommandLineRunner{

	public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
		log.info("Start cim route success!!!");
	}

	@Override
	public void run(String... args) throws Exception {
	}
}