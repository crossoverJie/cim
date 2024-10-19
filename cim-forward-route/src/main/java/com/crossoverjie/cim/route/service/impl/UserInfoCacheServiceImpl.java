package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.crossoverjie.cim.route.constant.Constant.ACCOUNT_PREFIX;
import static com.crossoverjie.cim.route.constant.Constant.LOGIN_STATUS_PREFIX;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/24 11:06
 * @since JDK 1.8
 */

@Service
public class UserInfoCacheServiceImpl implements UserInfoCacheService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate ;

    @Resource(name = "userInfoCache")
    private LoadingCache<Long, Optional<CIMUserInfo>> userInfoMap;

    @Override
    public Optional<CIMUserInfo> loadUserInfoByUserId(Long userId) {
        //Retrieve user information using a second-level cache.
        return userInfoMap.getUnchecked(userId);
    }

    @Override
    public boolean saveAndCheckUserLoginStatus(Long userId) throws Exception {

        Long add = redisTemplate.opsForSet().add(LOGIN_STATUS_PREFIX, userId.toString());
        if (add == 0){
            return false ;
        }else {
            return true ;
        }
    }

    @Override
    public Set<CIMUserInfo> onlineUser() {
        Set<CIMUserInfo> set = null ;
        Set<String> members = redisTemplate.opsForSet().members(LOGIN_STATUS_PREFIX);
        for (String member : members) {
            if (set == null){
                set = new HashSet<>(64) ;
            }
            Optional<CIMUserInfo> cimUserInfo = loadUserInfoByUserId(Long.valueOf(member)) ;

            cimUserInfo.ifPresent(set::add);
        }

        return set;
    }

}
