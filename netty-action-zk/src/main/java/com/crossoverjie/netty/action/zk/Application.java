package com.crossoverjie.netty.action.zk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@SpringBootApplication
public class Application implements CommandLineRunner{

	private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);



	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
		LOGGER.info("启动应用成功");
	}

	@Override
	public void run(String... args) throws Exception {
	}
}