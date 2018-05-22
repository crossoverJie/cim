package com.crossoverjie.netty.action;

import com.crossoverjie.netty.action.server.HeartBeatServer;
import io.netty.channel.ChannelFuture;
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
public class Application{

	private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
		LOGGER.info("启动成功");
	}

}