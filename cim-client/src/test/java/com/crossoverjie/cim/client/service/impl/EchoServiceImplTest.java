package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.CIMClientApplication;
import com.crossoverjie.cim.client.service.EchoService;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = CIMClientApplication.class)
@RunWith(SpringRunner.class)
public class EchoServiceImplTest {

    @Resource
    private EchoService echoService;

    @Test
    public void echo(){
        echoService.echo("test");
    }
}