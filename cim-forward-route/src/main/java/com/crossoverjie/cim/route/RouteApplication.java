package com.crossoverjie.cim.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@SpringBootApplication
public class RouteApplication {

	private final static Logger LOGGER = LoggerFactory.getLogger(RouteApplication.class);

	public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
		LOGGER.info("启动 route 成功");
	}

}