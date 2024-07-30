package com.crossoverjie.cim.route;

import com.crossoverjie.cim.route.kit.ServerListListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@Slf4j
@SpringBootApplication
public class RouteApplication implements CommandLineRunner{

	public static void main(String[] args) {
        SpringApplication.run(RouteApplication.class, args);
		log.info("Start cim route success!!!");
	}

	@Override
	public void run(String... args) throws Exception {

		//监听服务
		Thread thread = new Thread(new ServerListListener());
		thread.setName("zk-listener");
		thread.start() ;
	}
}