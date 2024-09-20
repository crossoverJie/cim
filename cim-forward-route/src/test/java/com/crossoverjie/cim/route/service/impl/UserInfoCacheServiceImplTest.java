package com.crossoverjie.cim.route.service.impl;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = RouteApplication.class)
public class UserInfoCacheServiceImplTest extends AbstractBaseTest{

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Test
    public void checkUserLoginStatus() throws Exception {
        boolean status = userInfoCacheService.saveAndCheckUserLoginStatus(2000L);
        log.info("status={}", status);
    }

    @Test
    public void onlineUser(){
        Set<CIMUserInfo> cimUserInfos = userInfoCacheService.onlineUser();
        log.info("cimUserInfos={}", JSON.toJSONString(cimUserInfos));
    }

}