package com.crossoverjie.cim.route.config;

import com.crossoverjie.cim.common.core.proxy.RpcProxyManager;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.metastore.ZkConfiguration;
import com.crossoverjie.cim.common.metastore.ZkMetaStoreImpl;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.crossoverjie.cim.server.api.ServerApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.crossoverjie.cim.route.constant.Constant.ACCOUNT_PREFIX;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 00:25
 * @since JDK 1.8
 */
@Configuration
@Slf4j
public class BeanConfig {


    @Resource
    private AppConfiguration appConfiguration;


    /**
     * Redis bean
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> stringObjectRedisTemplate(RedisConnectionFactory factory) {
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

    @Bean
    public Jackson2HashMapper hashMapper(ObjectMapper objectMapper) {
        // false 表示不在 key 里加 @class 信息
        return new Jackson2HashMapper(objectMapper, false);
    }

    @Bean
    public MetaStore metaStore() throws Exception {
        MetaStore metaStore = new ZkMetaStoreImpl();
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        metaStore.initialize(ZkConfiguration.builder()
                .metaServiceUri(appConfiguration.getZkAddr())
                .timeoutMs(appConfiguration.getZkConnectTimeout())
                .retryPolicy(retryPolicy)
                .build());
        metaStore.listenServerList((root, currentChildren) -> {
            log.info("Server list change, root=[{}], current server list=[{}]", root, currentChildren);
        });
        return metaStore;
    }


    /**
     * Redis bean
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    /**
     * http client
     *
     * @return okHttp
     */
    @Bean
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        return builder.build();
    }

    @Bean
    public RouteHandle buildRouteHandle() throws Exception {
        String routeWay = appConfiguration.getRouteWay();
        RouteHandle routeHandle = (RouteHandle) Class.forName(routeWay).newInstance();
        log.info("Current route algorithm is [{}]", routeHandle.getClass().getSimpleName());
        if (routeWay.contains("ConsistentHash")) {
            //一致性 hash 算法
            Method method = Class.forName(routeWay).getMethod("setHash", AbstractConsistentHash.class);
            AbstractConsistentHash consistentHash = (AbstractConsistentHash)
                    Class.forName(appConfiguration.getConsistentHashWay()).newInstance();
            method.invoke(routeHandle, consistentHash);
            return routeHandle;
        } else {

            return routeHandle;

        }

    }

    @Bean("userInfoCache")
    public LoadingCache<Long, Optional<CIMUserInfo>> userInfoCache(RedisTemplate<String, String> redisTemplate) {
        return CacheBuilder.newBuilder()
                .initialCapacity(64)
                .maximumSize(1024)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Optional<CIMUserInfo> load(Long userId) throws Exception {
                        String sendUserName = redisTemplate.opsForValue().get(ACCOUNT_PREFIX + userId);
                        if (sendUserName == null) {
                            return Optional.empty();
                        }
                        CIMUserInfo cimUserInfo = new CIMUserInfo(userId, sendUserName);
                        return Optional.of(cimUserInfo);
                    }
                });
    }
      
    @Bean
    public ServerApi serverApi(OkHttpClient okHttpClient) {
        return RpcProxyManager.create(ServerApi.class, okHttpClient);
    }
}
