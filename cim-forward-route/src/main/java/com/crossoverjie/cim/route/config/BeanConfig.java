package com.crossoverjie.cim.route.config;

import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.route.algorithm.consistenthash.AbstractConsistentHash;
import com.crossoverjie.cim.route.resource.RedisDistributionLock;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.MsgStoreService;
import com.crossoverjie.cim.route.util.SpringBeanFactory;
import com.crossoverjie.cim.route.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.vo.store.StoreMsgVo;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import okhttp3.OkHttpClient;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;

import static com.crossoverjie.cim.route.constant.Constant.OFFLINE_MESSAGE_PREFIX;
import static com.crossoverjie.cim.route.constant.Constant.ROUTE_PREFIX;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 00:25
 * @since JDK 1.8
 */
@Configuration
public class BeanConfig {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    AccountService accountService;

    @Bean
    public ZkClient buildZKClient() {
        return new ZkClient(appConfiguration.getZkAddr(), appConfiguration.getZkConnectTimeout());
    }

    @Bean
    public LoadingCache<String, String> buildCache() {
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return null;
                    }
                });
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

    /**
     * 创建回调线程池
     *
     * @return
     */
    @Bean("loopSendOfflineMsg")
    public Thread buildCallerThread() {
        Thread loopSendOfflineThread = new Thread() {
            @Override
            public void run() {
                RedisDistributionLock distributionLock = SpringBeanFactory.getBean("redisDistributionLock", RedisDistributionLock.class);
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!distributionLock.lock("loopSendOfflineMsg")) {
                        continue;
                    }

                    RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
                    ScanOptions options = ScanOptions.scanOptions()
                            .match(OFFLINE_MESSAGE_PREFIX + "*")
                            .build();
                    Cursor<byte[]> scan = connection.scan(options);

                    while (scan.hasNext()) {
                        byte[] next = scan.next();
                        String key = new String(next, StandardCharsets.UTF_8);
                        String userIdStr = key.replaceFirst(OFFLINE_MESSAGE_PREFIX, "");
                        Long userId = Long.valueOf(userIdStr);
                        sendUserOfflineMsg(userId);
                    }
                    distributionLock.unlock("distributionLock");
                }
            }
        };
        loopSendOfflineThread.start();
        return loopSendOfflineThread;
    }

    private void sendUserOfflineMsg(Long userId) {
        MsgStoreService msgStoreService = SpringBeanFactory.getBean(MsgStoreService.class);

        //一次最多发送200条，超出的，下次发送
        List<String> msgUUIDList = msgStoreService.getUserOffLineMessageUUId(userId, 200);
        if (msgUUIDList == null || msgUUIDList.isEmpty())
            return;

        String lastSendSuccessUUID = null;
        for (String msgUUID : msgUUIDList) {
            StoreMsgVo storeMsgVo = msgStoreService.getMessage(msgUUID);
            if (storeMsgVo == null)
                continue;

            try {
                //获取接收消息用户的路由信息
                CIMServerResVO cimServerResVO = accountService.loadRouteRelatedByUserId(userId);
                //推送消息
                String url = "http://" + cimServerResVO.getIp() + ":" + cimServerResVO.getHttpPort() + "/sendMsg";

                //p2pRequest.getReceiveUserId()==>消息接收者的 userID
                ChatReqVO chatVO = new ChatReqVO(userId, storeMsgVo.getMsg());
                BaseResponse baseResponse = accountService.pushMsg(url, storeMsgVo.getSenderUserId(), chatVO);

                if (baseResponse != null && baseResponse.isSuccess()) {
                    //发送成功，记录最后一条发送成功的uuid
                    lastSendSuccessUUID = msgUUID;
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }//end for msgUUID

        //清除最后一条发送成功的uuid，更早之前的消息也跟着被移除了
        if (lastSendSuccessUUID != null)
            msgStoreService.setUserOffLineMessageRead(userId,lastSendSuccessUUID);
    }

}
