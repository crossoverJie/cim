package com.crossoverjie.cim.server.zk.util;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.server.zk.cache.ServerCache;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Function: Zookeeper 工具
 *
 * @author crossoverJie
 *         Date: 2018/8/19 00:33
 * @since JDK 1.8
 */
@Component
public class ZKit {

    private static Logger logger = LoggerFactory.getLogger(ZKit.class);


    @Autowired
    private ZkClient zkClient;

    @Autowired
    private AppConfiguration appConfiguration ;

    @Autowired
    private ServerCache serverCache ;


    /**
     * 创建父级节点
     */
    public void createRootNode(){
        boolean exists = zkClient.exists(appConfiguration.getZkRoot());
        if (exists){
            return;
        }

        //创建 root
        zkClient.createPersistent(appConfiguration.getZkRoot()) ;
    }

    /**
     * 写入指定节点 临时目录
     *
     * @param path
     * @param value
     */
    public void createNode(String path, String value) {
        zkClient.createEphemeral(path, value);
    }


    /**
     * 监听事件
     *
     * @param path
     */
    public void subscribeEvent(String path) {
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                logger.info("清除/更新本地缓存 parentPath=【{}】,currentChilds=【{}】", parentPath,currentChilds.toString());

                //更新所有缓存/先删除 再新增
                serverCache.updateCache(currentChilds) ;
            }
        });


    }


    /**
     * 获取所有注册节点
     * @return
     */
    public List<String> getAllNode(){
        List<String> children = zkClient.getChildren("/route");
        logger.info("查询所有节点成功=【{}】", JSON.toJSONString(children));
       return children;
    }

    /**
     * 关闭 ZK
     */
    public void closeZK() {
        logger.info("正在关闭 ZK");
        zkClient.close();
        logger.info("关闭 ZK 成功");

    }
}
