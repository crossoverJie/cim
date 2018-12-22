package com.crossoverjie.cim.server.kit;

import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/24 01:37
 * @since JDK 1.8
 */
public class RegistryZK implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(RegistryZK.class);

    private ZKit zKit;

    private AppConfiguration appConfiguration ;

    private String ip;
    private int port;

    public RegistryZK(String ip, int port) {
        this.ip = ip;
        this.port = port;
        zKit = SpringBeanFactory.getBean(ZKit.class) ;
        appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class) ;
    }

    @Override
    public void run() {

        //创建父节点
        zKit.createRootNode();

        //是否要将自己注册到 ZK
        if (appConfiguration.isZkSwitch()){
            String path = appConfiguration.getZkRoot() + "/ip-" + ip + ":" + port;
            zKit.createNode(path, path);
            logger.info("注册 zookeeper 成功，msg=[{}]", path);
        }


    }
}