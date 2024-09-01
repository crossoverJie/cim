package com.crossoverjie.cim.client;

import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.util.SpringBeanFactory;
import com.crossoverjie.cim.route.AbstractRouteBaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
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
        super.startServer();
        super.startRoute();
        this.login("crossoverJie", 8082);
        this.login("cj", 8182);
        MsgHandle msgHandle = SpringBeanFactory.getBean(MsgHandle.class);
        msgHandle.innerCommand(":olu");
    }

    @Test
    public void groupChat() throws Exception {
        super.startServer();
        super.startRoute();
        this.login("crossoverJie", 8082);
        this.login("cj", 8182);
        MsgHandle msgHandle = SpringBeanFactory.getBean(MsgHandle.class);
        msgHandle.sendMsg("hello");
    }

}
