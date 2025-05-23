package com.crossoverjie.cim.client;

import com.crossoverjie.cim.client.scanner.Scan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author crossoverJie
 */
@Slf4j
@SpringBootApplication
public class CIMClientApplication implements CommandLineRunner{


	public static void main(String[] args) {
        SpringApplication.run(CIMClientApplication.class, args);
		log.info("Client start success");
	}

	@Override
	public void run(String... args) {
		Scan scan = new Scan() ;
		Thread thread = new Thread(scan);
		thread.setName("scan-thread");
		thread.start();
	}
}