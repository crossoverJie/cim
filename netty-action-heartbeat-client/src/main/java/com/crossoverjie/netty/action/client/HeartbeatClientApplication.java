package com.crossoverjie.netty.action.client;

import com.crossoverjie.netty.action.common.pojo.CustomProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@SpringBootApplication
public class HeartbeatClientApplication implements CommandLineRunner{

	private final static Logger LOGGER = LoggerFactory.getLogger(HeartbeatClientApplication.class);


	@Autowired
	private HeartbeatClient heartbeatClient ;

	public static void main(String[] args) {
        SpringApplication.run(HeartbeatClientApplication.class, args);
		LOGGER.info("启动 Client 成功");
	}

	@Override
	public void run(String... args) throws Exception {
		heartbeatClient.sendMsg(new CustomProtocol(999999L,"ping"));
	}
}