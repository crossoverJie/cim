package com.crossoverjie.cim.server.zk.config;

import com.crossoverjie.cim.server.zk.util.AppConfiguration;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/24 01:28
 * @since JDK 1.8
 */
@Configuration
public class AppConfig {

    @Autowired
    private AppConfiguration appConfiguration ;

    @Bean
    public ZkClient buildZKClient(){
        return new ZkClient(appConfiguration.getZkAddr(), 5000);
    }


    @Bean
    public LoadingCache<String,String> buildCache(){
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return null;
                    }
                });
    }
}
