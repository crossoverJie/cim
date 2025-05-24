package com.crossoverjie.cim.server;

import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.kit.RegistryMetaStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;

/**
 * @author crossoverJie
 */
@EnableScheduling
//
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		RedissonAutoConfiguration.class
})
@Slf4j
public class CIMServerApplication implements CommandLineRunner{


	@Resource
	private AppConfiguration appConfiguration ;

	@Resource
	private MetaStore metaStore;

	@Value("${server.port}")
	private int httpPort ;

	public static void main(String[] args) {
        SpringApplication.run(CIMServerApplication.class, args);
		log.info("Start cim server success!!!");
	}

	@Override
	public void run(String... args) throws Exception {
		String addr = InetAddress.getLocalHost().getHostAddress();
		Thread thread = new Thread(new RegistryMetaStore(metaStore, addr, appConfiguration.getCimServerPort(),httpPort));
		thread.setName("registry-zk");
		thread.start() ;
	}
}