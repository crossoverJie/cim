package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.common.core.proxy.RpcProxyManager;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.metastore.ZkMetaStoreImpl;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.route.api.RouteApi;
import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 00:25
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig {

    @Resource
    private AppConfiguration appConfiguration;

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
    public MetaStore metaStore() {
        return new ZkMetaStoreImpl();
    }

    /**
     * Redis bean
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // 设置键的序列化器为字符串
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 设置值的序列化器为JSON，支持对象存储
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置Hash键和值的序列化器（可选）
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 初始化属性
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 创建心跳单例
     * @return
     */
    @Bean(value = "heartBeat")
    public Request heartBeat() {
        return Request.newBuilder()
                .setRequestId(0L)
                .setReqMsg("pong")
                .setCmd(BaseCommand.PING)
                .build();
    }

    @Bean
    public RouteApi routeApi(OkHttpClient okHttpClient) {
        return RpcProxyManager.create(RouteApi.class, appConfiguration.getRouteUrl(), okHttpClient);
    }
}
