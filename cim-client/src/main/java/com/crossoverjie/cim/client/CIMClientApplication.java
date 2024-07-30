package com.crossoverjie.cim.client;

import com.crossoverjie.cim.client.scanner.Scan;
import com.crossoverjie.cim.client.service.impl.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@Slf4j
@SpringBootApplication
public class CIMClientApplication implements CommandLineRunner{

	@Autowired
	private ClientInfo clientInfo ;
	public static void main(String[] args) {
        SpringApplication.run(CIMClientApplication.class, args);
		log.info("启动 Client 服务成功");
	}

	@Override
	public void run(String... args) throws Exception {
		Scan scan = new Scan() ;
		Thread thread = new Thread(scan);
		thread.setName("scan-thread");
		thread.start();
		clientInfo.saveStartDate();
	}
}