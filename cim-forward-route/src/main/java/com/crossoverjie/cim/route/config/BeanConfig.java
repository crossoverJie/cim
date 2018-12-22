package com.crossoverjie.cim.route.config;

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
 *         Date: 2018/12/23 00:25
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig {

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
