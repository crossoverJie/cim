package com.crossoverjie.cim.route.kit;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.route.config.AppConfiguration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Function: Zookeeper kit
 *
 * @author crossoverJie
 *         Date: 2018/8/19 00:33
 * @since JDK 1.8
 */
@Component
@Slf4j
public class ZKit {



    @Autowired
    private ZkClient zkClient;

    @Autowired
    private AppConfiguration appConfiguration;




    /**
     * get all server node from zookeeper
     * @return
     */
    public List<String> getAllNode(){
        List<String> children = zkClient.getChildren(appConfiguration.getZkRoot());
        log.info("Query all node =[{}] success.", JSON.toJSONString(children));
       return children;
    }


}
