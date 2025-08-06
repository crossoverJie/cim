package com.crossoverjie.cim.server;

import com.crossoverjie.cim.common.enums.RegistryType;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.kit.RegistryMetaStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class CIMServerApplication implements CommandLineRunner {


    @Resource
    private AppConfiguration appConfiguration;

    @Autowired(required = false)
    private MetaStore metaStore;

    @Value("${server.port}")
    private int httpPort;

    public static void main(String[] args) {
        SpringApplication.run(CIMServerApplication.class, args);
        log.info("Start cim server success!!!");
    }

    @Override
    public void run(String... args) throws Exception {
        if (StringUtils.equals(appConfiguration.getRegisterType().getCode(), RegistryType.NO.getCode())) {
            log.info("no register type,client need direct connection!");
            return;
        }
        String addr = InetAddress.getLocalHost().getHostAddress();
        Thread thread = new Thread(new RegistryMetaStore(metaStore, addr, appConfiguration.getCimServerPort(), httpPort));
        thread.setName("registry-zk");
        thread.start();
    }
}