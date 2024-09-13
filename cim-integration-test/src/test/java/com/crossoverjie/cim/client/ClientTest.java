package com.crossoverjie.cim.client;

import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.util.SpringBeanFactory;
import com.crossoverjie.cim.client.sdk.route.AbstractRouteBaseTest;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.SpringApplication;

@Slf4j
public class ClientTest extends AbstractRouteBaseTest {


    private void login(String userName, int port) throws Exception {
        Long userId = super.registerAccount(userName);
        SpringApplication client = new SpringApplication(CIMClientApplication.class);
        client.setAdditionalProfiles("client");
        String[] args = new String[]{
                "--server.port=" + port,
                "--cim.user.id=" + userId,
                "--cim.user.userName=" + userName
        };
        client.run(args);
    }

    @Test
    public void olu() throws Exception {
        super.starSingleServer();
        super.startRoute();
        this.login("crossoverJie", 8082);
        this.login("cj", 8182);
        MsgHandle msgHandle = SpringBeanFactory.getBean(MsgHandle.class);
        msgHandle.innerCommand(":olu");
        msgHandle.sendMsg("hello");
        TimeUnit.SECONDS.sleep(1);
    }

    @AfterEach
    public void tearDown() {
        super.close();
    }


}
