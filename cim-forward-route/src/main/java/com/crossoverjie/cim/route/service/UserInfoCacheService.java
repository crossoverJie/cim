package com.crossoverjie.cim.route.service;

import com.crossoverjie.cim.common.pojo.CIMUserInfo;

import java.util.Optional;
import java.util.Set;

public interface UserInfoCacheService {

    Optional<CIMUserInfo> loadUserInfoByUserId(Long userId);

    boolean saveAndCheckUserLoginStatus(Long userId) throws Exception;

    Set<CIMUserInfo> onlineUser();

    Set<Long> getRegisteredUserIds();
}
