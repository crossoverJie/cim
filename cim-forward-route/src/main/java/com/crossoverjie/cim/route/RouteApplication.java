package com.crossoverjie.cim.route;

import com.crossoverjie.cim.route.kit.ServerListListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@SpringBootApplication
public class RouteApplication implements CommandLineRunner{

	private final static Logger LOGGER = LoggerFactory.getLogger(RouteApplication.class);

	public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
		LOGGER.info("Start cim route success!!!");
	}

	@Override
	public void run(String... args) throws Exception {

		//监听服务
		Thread thread = new Thread(new ServerListListener());
		thread.setName("zk-listener");
		thread.start() ;
	}
}