package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.metastore.ZkConfiguration;
import com.crossoverjie.cim.common.metastore.ZkMetaStoreImpl;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import okhttp3.OkHttpClient;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 00:25
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig {

    /**
     * http client
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    @Bean
    public MetaStore metaStore() throws Exception {
        return new ZkMetaStoreImpl();
    }


    /**
     * 创建心跳单例
     * @return
     */
    @Bean(value = "heartBeat")
    public CIMRequestProto.CIMReqProtocol heartBeat() {
        CIMRequestProto.CIMReqProtocol heart = CIMRequestProto.CIMReqProtocol.newBuilder()
                .setRequestId(0L)
                .setReqMsg("pong")
                .setType(Constants.CommandType.PING)
                .build();
        return heart;
    }
}
