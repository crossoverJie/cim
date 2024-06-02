package com.crossoverjie.cim.server;

import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.kit.RegistryZK;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

/**
 * @author crossoverJie
 */
@SpringBootApplication
@Slf4j
public class CIMServerApplication implements CommandLineRunner{


	@Autowired
	private AppConfiguration appConfiguration ;

	@Value("${server.port}")
	private int httpPort ;

	public static void main(String[] args) {
        SpringApplication.run(CIMServerApplication.class, args);
		log.info("Start cim server success!!!");
	}

	@Override
	public void run(String... args) throws Exception {
		//获得本机IP
		String addr = InetAddress.getLocalHost().getHostAddress();
		Thread thread = new Thread(new RegistryZK(addr, appConfiguration.getCimServerPort(),httpPort));
		thread.setName("registry-zk");
		thread.start() ;
	}
}