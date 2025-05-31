package com.crossoverjie.cim.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author crossoverJie
 */
@Slf4j
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = {
		"com.crossoverjie.cim.route",
		"com.crossoverjie.cim.persistence",
		"com.crossoverjie.cim.common"
})
public class RouteApplication implements CommandLineRunner{

	public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
		log.info("Start cim route success!!!");
	}

	@Override
	public void run(String... args) throws Exception {
	}
}