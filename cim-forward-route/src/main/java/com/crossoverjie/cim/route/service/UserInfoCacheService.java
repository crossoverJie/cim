package com.crossoverjie.cim.route.service;

import com.crossoverjie.cim.common.pojo.CIMUserInfo;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/24 11:06
 * @since JDK 1.8
 */
public interface UserInfoCacheService {

    /**
     * 通过 userID 获取用户信息
     * @param userId 用户唯一 ID
     * @return
     * @throws Exception
     */
    CIMUserInfo loadUserInfoByUserId(long userId) throws Exception ;
}
