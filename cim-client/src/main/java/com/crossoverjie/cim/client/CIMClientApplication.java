package com.crossoverjie.cim.client;

import com.crossoverjie.cim.client.scanner.Scan;
import com.crossoverjie.cim.client.service.impl.ClientInfo;
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
public class CIMClientApplication implements CommandLineRunner{

	private final static Logger LOGGER = LoggerFactory.getLogger(CIMClientApplication.class);

	@Autowired
	private ClientInfo clientInfo ;
	public static void main(String[] args) {
        SpringApplication.run(CIMClientApplication.class, args);
		LOGGER.info("启动 Client 服务成功");
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