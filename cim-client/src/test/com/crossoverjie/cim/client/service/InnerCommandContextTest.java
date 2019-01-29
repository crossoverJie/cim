package com.crossoverjie.cim.client.service;

import com.crossoverjie.cim.client.CIMClientApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = CIMClientApplication.class)
@RunWith(SpringRunner.class)
public class InnerCommandContextTest {

    @Autowired
    private InnerCommandContext context;

    @Test
    public void execute() {
        String msg = ":all";
        InnerCommand execute = context.execute(msg);
        execute.process(msg) ;
    }
}