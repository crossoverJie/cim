package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.route.RouteApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest(classes = RouteApplication.class)
public class RedisTest extends AbstractBaseTest {

    @Autowired
    private RedisTemplate<String,String> redisTemplate ;

    @Test
    public void test(){
        redisTemplate.opsForValue().set("test","test") ;
        String test = redisTemplate.opsForValue().get("test");
        Assertions.assertEquals("test",test);
    }
}
