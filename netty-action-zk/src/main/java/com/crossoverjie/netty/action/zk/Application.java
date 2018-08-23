package com.crossoverjie.netty.action.zk;

import com.crossoverjie.netty.action.zk.thread.RegistryZK;
import com.crossoverjie.netty.action.zk.util.AppConfiguration;
import com.crossoverjie.netty.action.zk.util.ZKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

/**
 * @author crossoverJie
 */
@SpringBootApplication
public class Application implements CommandLineRunner{

	private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);

	@Autowired
	private AppConfiguration appConfiguration ;

	@Autowired
	private static ZKUtil zkUtil ;

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
		LOGGER.info("启动应用成功");

	}

	@Override
	public void run(String... args) throws Exception {
		//获得本机IP
		String addr = InetAddress.getLocalHost().getHostAddress();
		Thread thread = new Thread(new RegistryZK(addr, appConfiguration.getPort()));
		thread.setName("registry-zk");
		thread.start() ;
	}
}