package com.crossoverjie.netty.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@SpringBootApplication
public class HeartbeatServerApplication {

	private final static Logger LOGGER = LoggerFactory.getLogger(HeartbeatServerApplication.class);

	public static void main(String[] args) {
        SpringApplication.run(HeartbeatServerApplication.class, args);
		LOGGER.info("启动 Server 成功");
	}

}