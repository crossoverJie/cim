package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.crossoverjie.cim.route.constant.Constant.ACCOUNT_PREFIX;
import static com.crossoverjie.cim.route.constant.Constant.LOGIN_STATUS_PREFIX;

@Slf4j
@Service
public class UserInfoCacheServiceImpl implements UserInfoCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "userInfoCache")
    private LoadingCache<Long, Optional<CIMUserInfo>> userInfoMap;

    @Override
    public Optional<CIMUserInfo> loadUserInfoByUserId(Long userId) {
        return userInfoMap.get(userId);
    }

    @Override
    public boolean saveAndCheckUserLoginStatus(Long userId) throws Exception {
        Long add = redisTemplate.opsForSet().add(LOGIN_STATUS_PREFIX, userId.toString());
        return add != 0;
    }

    @Override
    public Set<CIMUserInfo> onlineUser() {
        Set<CIMUserInfo> set = null;
        Set<String> members = redisTemplate.opsForSet().members(LOGIN_STATUS_PREFIX);
        for (String member : members) {
            if (set == null) {
                set = new HashSet<>(64);
            }
            try {
                Optional<CIMUserInfo> cimUserInfo = loadUserInfoByUserId(Long.valueOf(member));
                cimUserInfo.ifPresent(set::add);
            } catch (NumberFormatException e) {
                log.warn("Skipping invalid user ID format in Redis set: {}", member);
            }
        }

        return set;
    }

    @Override
    public Set<Long> getRegisteredUserIds() {
        Set<Long> userIds = new HashSet<>();
        Set<String> keys = redisTemplate.keys(ACCOUNT_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                try {
                    String userIdStr = key.replace(ACCOUNT_PREFIX, "");
                    userIds.add(Long.parseLong(userIdStr));
                } catch (NumberFormatException e) {
                    log.warn("Skipping invalid account key: {}", key);
                }
            }
        }
        return userIds;
    }
}
