package com.crossoverjie.cim.server.kit;

import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;


/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/24 01:37
 * @since JDK 1.8
 */
@Slf4j
public class RegistryZK implements Runnable {


    private ZKit zKit;

    private AppConfiguration appConfiguration ;

    private String ip;
    private int cimServerPort;
    private int httpPort;

    public RegistryZK(String ip, int cimServerPort,int httpPort) {
        this.ip = ip;
        this.cimServerPort = cimServerPort;
        this.httpPort = httpPort ;
        zKit = SpringBeanFactory.getBean(ZKit.class) ;
        appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class) ;
    }

    @Override
    public void run() {

        //创建父节点
        zKit.createRootNode();

        //是否要将自己注册到 ZK
        if (appConfiguration.isZkSwitch()){
            String path = appConfiguration.getZkRoot() + "/ip-" + ip + ":" + cimServerPort + ":" + httpPort;
            zKit.createNode(path);
            log.info("Registry zookeeper success, msg=[{}]", path);
        }


    }
}