package com.crossoverjie.cim.server.kit;

import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.metastore.ZkConfiguration;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/24 01:37
 * @since JDK 1.8
 */
@Slf4j
public class RegistryMetaStore implements Runnable {


    private MetaStore metaStore;

    private AppConfiguration appConfiguration ;

    private String ip;
    private int cimServerPort;
    private int httpPort;
    public RegistryMetaStore(MetaStore metaStore, String ip, int cimServerPort, int httpPort) {
        this.ip = ip;
        this.cimServerPort = cimServerPort;
        this.httpPort = httpPort ;
        this.metaStore = metaStore;
        appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class) ;
    }

    @SneakyThrows
    @Override
    public void run() {

        if (!appConfiguration.isZkSwitch()){
            log.info("Skip registry to metaStore");
            return;
        }

        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        metaStore.initialize(ZkConfiguration.builder()
                .metaServiceUri(appConfiguration.getZkAddr())
                .timeoutMs(appConfiguration.getZkConnectTimeout())
                .retryPolicy(retryPolicy)
                .build());
        metaStore.addServer(ip, cimServerPort, httpPort);
    }
}