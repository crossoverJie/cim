package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.crossoverjie.cim.route.constant.Constant.ACCOUNT_PREFIX;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/24 11:06
 * @since JDK 1.8
 */
@Service
public class UserInfoCacheServiceImpl implements UserInfoCacheService {

    /**
     * 本地缓存，后期可换为 LRU
     */
    private final static Map<Long,CIMUserInfo> USER_INFO_MAP = new ConcurrentHashMap<>(64) ;

    @Autowired
    private RedisTemplate<String,String> redisTemplate ;

    @Override
    public CIMUserInfo loadUserInfo(long userId) throws Exception {

        //优先从本地缓存获取
        CIMUserInfo cimUserInfo = USER_INFO_MAP.get(userId);
        if (cimUserInfo != null){
            return cimUserInfo ;
        }

        //load redis
        String sendUserName = redisTemplate.opsForValue().get(ACCOUNT_PREFIX + userId);
        if (sendUserName != null){
            cimUserInfo = new CIMUserInfo(userId,sendUserName) ;
            USER_INFO_MAP.put(userId,cimUserInfo) ;
        }

        return cimUserInfo;
    }
}
