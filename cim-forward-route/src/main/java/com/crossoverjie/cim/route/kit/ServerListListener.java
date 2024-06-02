package com.crossoverjie.cim.route.kit;

import com.crossoverjie.cim.route.cache.ServerCache;
import com.crossoverjie.cim.route.config.AppConfiguration;
import com.crossoverjie.cim.route.util.SpringBeanFactory;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 00:35
 * @since JDK 1.8
 */
@Slf4j
public class ServerListListener implements Runnable{


    private ZKit zkUtil;

    private AppConfiguration appConfiguration ;

    private ZkClient zkClient;

    private ServerCache serverCache ;


    public ServerListListener() {
        zkUtil = SpringBeanFactory.getBean(ZKit.class) ;
        appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class) ;
        zkClient = SpringBeanFactory.getBean(ZkClient.class) ;
        serverCache = SpringBeanFactory.getBean(ServerCache.class) ;
    }

    @Override
    public void run() {
        //注册监听服务
        subscribeEvent(appConfiguration.getZkRoot());

    }

    /**
     * 监听事件
     *
     * @param path
     */
    public void subscribeEvent(String path) {
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
                log.info("Clear and update local cache parentPath=[{}],currentChildren=[{}]", parentPath,currentChildren.toString());

                //update local cache, delete and save.
                serverCache.updateCache(currentChildren) ;
            }
        });


    }
}
