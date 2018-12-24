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
     * @param userId
     * @return
     * @throws Exception
     */
    CIMUserInfo loadUserInfo(long userId) throws Exception ;
}
