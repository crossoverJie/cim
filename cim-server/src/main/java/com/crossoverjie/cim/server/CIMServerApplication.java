package com.crossoverjie.cim.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@SpringBootApplication
public class CIMServerApplication {

	private final static Logger LOGGER = LoggerFactory.getLogger(CIMServerApplication.class);

	public static void main(String[] args) {
        SpringApplication.run(CIMServerApplication.class, args);
		LOGGER.info("启动 Server 成功");
	}

}