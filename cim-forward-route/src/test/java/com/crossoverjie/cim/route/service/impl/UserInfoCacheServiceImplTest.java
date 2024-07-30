package com.crossoverjie.cim.route.service.impl;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@Slf4j
@SpringBootTest(classes = RouteApplication.class)
@RunWith(SpringRunner.class)
public class UserInfoCacheServiceImplTest {

    @Autowired
    private UserInfoCacheService userInfoCacheService;

    @Test
    public void checkUserLoginStatus() throws Exception {
        boolean status = userInfoCacheService.saveAndCheckUserLoginStatus(2000L);
        log.info("status={}", status);
    }

    @Test
    public void removeLoginStatus() throws Exception {
        userInfoCacheService.removeLoginStatus(2000L);
    }

    @Test
    public void onlineUser(){
        Set<CIMUserInfo> cimUserInfos = userInfoCacheService.onlineUser();
        log.info("cimUserInfos={}", JSON.toJSONString(cimUserInfos));
    }

}