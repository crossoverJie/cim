package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.client.sdk.impl.ClientImpl;
import com.crossoverjie.cim.client.sdk.route.AbstractRouteBaseTest;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.constant.Constant;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class OfflineMsgTest extends AbstractRouteBaseTest {

    @Test
    public void testP2POfflineChatRedis() throws Exception {
        super.starSingleServer();
        super.startRoute(Constant.OfflineStoreMode.REDIS);
        String routeUrl = "http://localhost:8083";
        String cj = "cj";
        String ls = "ls";
        Long cjId = super.registerAccount(cj);
        Long lsId = super.registerAccount(ls);
        var auth1 = ClientConfigurationData.Auth.builder()
                .userName(cj)
                .userId(cjId)
                .build();
        var auth3 = ClientConfigurationData.Auth.builder()
                .userName(ls)
                .userId(lsId)
                .build();

        var client1Receive = new ArrayList<>();
        @Cleanup
        Client client1 = Client.builder()
                .auth(auth1)
                .routeUrl(routeUrl)
                .messageListener((__, properties, message) -> {
                    log.info("client1 receive message = {}", message);
                    client1Receive.add(message);
                })
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state = client1.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));
        Optional<CIMServerResVO> serverInfo = client1.getServerInfo();
        Assertions.assertTrue(serverInfo.isPresent());
        System.out.println("client1 serverInfo = " + serverInfo.get());


        // client3
        AtomicReference<String> client3Receive = new AtomicReference<>();
        Client client3 = Client.builder()
                .auth(auth3)
                .routeUrl(routeUrl)
                .messageListener((client, properties, message) -> {
                    log.info("client3 receive message = {}", message);
                    client3Receive.set(message);
                })
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state3 = client3.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state3));

        Optional<CIMServerResVO> serverInfo3 = client3.getServerInfo();
        Assertions.assertTrue(serverInfo3.isPresent());
        System.out.println("client3 serverInfo = " + serverInfo3.get());

        // client1 send msg to client3
        String msg = "hello";
        client1.sendP2P(P2PReqVO.builder()
                .receiveUserId(lsId)
                .msg(msg)
                .build());

        Set<CIMUserInfo> onlineUser = client1.getOnlineUser();
        Assertions.assertEquals(onlineUser.size(), 2);
        onlineUser.forEach(userInfo -> {
            log.info("online user = {}", userInfo);
            Long userId = userInfo.getUserId();
            if (userId.equals(cjId)) {
                Assertions.assertEquals(cj, userInfo.getUserName());
            } else if (userId.equals(lsId)) {
                Assertions.assertEquals(ls, userInfo.getUserName());
            }
        });


        Assertions.assertEquals(ClientImpl.getClientMap().size(), 2);
        Awaitility.await().untilAsserted(
                () -> Assertions.assertEquals(msg, client3Receive.get()));

        // Manually offline client3
        client3.close();
        client3Receive.set(null);
        ClientState.State closeState = client3.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Closed, closeState));

        // client1 send client3 an offline message
        String offlineMsg = "offline message";
        client1.sendP2P(P2PReqVO.builder()
                .receiveUserId(lsId)
                .msg(offlineMsg)
                .build());

        // online again
        client3 = Client.builder()
                .auth(auth3)
                .routeUrl(routeUrl)
                .messageListener((client, properties, message) -> {
                    log.info("client3 online again receive message = {}", message);
                    client3Receive.set(message);
                })
                .build();

        ClientState.State client3AgainState = client3.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, client3AgainState));


        // check offline message
        Awaitility.await().untilAsserted(
                () -> Assertions.assertEquals(offlineMsg, client3Receive.get()));

        // close
        super.stopSingle();
        client1.close();
        client3.close();
        Assertions.assertEquals(ClientImpl.getClientMap().size(), 0);
    }
}
