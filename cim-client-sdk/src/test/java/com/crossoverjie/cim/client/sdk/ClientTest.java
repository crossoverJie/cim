package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.client.sdk.io.backoff.RandomBackoff;
import com.crossoverjie.cim.client.sdk.route.AbstractRouteBaseTest;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ClientTest extends AbstractRouteBaseTest {


    @AfterEach
    public void tearDown() {
        super.close();
    }

    @Test
    public void groupChat() throws Exception {
        super.starSingleServer();
        super.startRoute();
        String routeUrl = "http://localhost:8083";
        String cj = "crossoverJie";
        String zs = "zs";
        Long id = super.registerAccount(cj);
        Long zsId = super.registerAccount(zs);
        var auth1 = ClientConfigurationData.Auth.builder()
                .userId(id)
                .userName(cj)
                .build();
        var auth2 = ClientConfigurationData.Auth.builder()
                .userId(zsId)
                .userName(zs)
                .build();

        @Cleanup
        Client client1 = Client.builder()
                .auth(auth1)
                .routeUrl(routeUrl)
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state = client1.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));
        Optional<CIMServerResVO> serverInfo = client1.getServerInfo();
        Assertions.assertTrue(serverInfo.isPresent());
        System.out.println("client1 serverInfo = " + serverInfo.get());


        AtomicReference<String> client2Receive = new AtomicReference<>();
        @Cleanup
        Client client2 = Client.builder()
                .auth(auth2)
                .routeUrl(routeUrl)
                .messageListener((client, properties, message) -> {
                    client2Receive.set(message);
                    Assertions.assertEquals(properties.get(Constants.MetaKey.USER_ID), String.valueOf(auth1.getUserId()));
                    Assertions.assertEquals(properties.get(Constants.MetaKey.USER_NAME), auth1.getUserName());
                })
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state2 = client2.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state2));

        Optional<CIMServerResVO> serverInfo2 = client2.getServerInfo();
        Assertions.assertTrue(serverInfo2.isPresent());
        System.out.println("client2 serverInfo = " + serverInfo2.get());

        // send msg
        String msg = "hello";
        client1.sendGroup(msg);

        Set<CIMUserInfo> onlineUser = client1.getOnlineUser();
        Assertions.assertEquals(onlineUser.size(), 2);
        onlineUser.forEach(userInfo -> {
            log.info("online user = {}", userInfo);
            Long userId = userInfo.getUserId();
            if (userId.equals(id)) {
                Assertions.assertEquals(cj, userInfo.getUserName());
            } else if (userId.equals(zsId)) {
                Assertions.assertEquals(zs, userInfo.getUserName());
            }
        });

        Awaitility.await().untilAsserted(
                () -> Assertions.assertEquals(msg, client2Receive.get()));
        super.stopSingle();
    }

    @Test
    public void testP2PChat() throws Exception {
        super.starSingleServer();
        super.startRoute();
        String routeUrl = "http://localhost:8083";
        String cj = "cj";
        String zs = "zs";
        String ls = "ls";
        Long cjId = super.registerAccount(cj);
        Long zsId = super.registerAccount(zs);
        Long lsId = super.registerAccount(ls);
        var auth1 = ClientConfigurationData.Auth.builder()
                .userName(cj)
                .userId(cjId)
                .build();
        var auth2 = ClientConfigurationData.Auth.builder()
                .userName(zs)
                .userId(zsId)
                .build();
        var auth3 = ClientConfigurationData.Auth.builder()
                .userName(ls)
                .userId(lsId)
                .build();

        @Cleanup
        Client client1 = Client.builder()
                .auth(auth1)
                .routeUrl(routeUrl)
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state = client1.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));
        Optional<CIMServerResVO> serverInfo = client1.getServerInfo();
        Assertions.assertTrue(serverInfo.isPresent());
        System.out.println("client1 serverInfo = " + serverInfo.get());


        // client2
        AtomicReference<String> client2Receive = new AtomicReference<>();
        @Cleanup
        Client client2 = Client.builder()
                .auth(auth2)
                .routeUrl(routeUrl)
                .messageListener((client, properties, message) -> client2Receive.set(message))
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state2 = client2.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state2));

        Optional<CIMServerResVO> serverInfo2 = client2.getServerInfo();
        Assertions.assertTrue(serverInfo2.isPresent());
        System.out.println("client2 serverInfo = " + serverInfo2.get());

        // client3
        AtomicReference<String> client3Receive = new AtomicReference<>();
        @Cleanup
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

        // send msg to client3
        String msg = "hello";
        client1.sendP2P(P2PReqVO.builder()
                .receiveUserId(lsId)
                .msg(msg)
                .build());

        Set<CIMUserInfo> onlineUser = client1.getOnlineUser();
        Assertions.assertEquals(onlineUser.size(), 3);
        onlineUser.forEach(userInfo -> {
            log.info("online user = {}", userInfo);
            Long userId = userInfo.getUserId();
            if (userId.equals(cjId)) {
                Assertions.assertEquals(cj, userInfo.getUserName());
            } else if (userId.equals(zsId)) {
                Assertions.assertEquals(zs, userInfo.getUserName());
            } else if (userId.equals(lsId)) {
                Assertions.assertEquals(ls, userInfo.getUserName());
            }
        });

        Awaitility.await().untilAsserted(
                () -> Assertions.assertEquals(msg, client3Receive.get()));
        Awaitility.await().untilAsserted(
                () -> Assertions.assertNull(client2Receive.get()));
        super.stopSingle();
    }

    /**
     * 1. Start two servers.
     * 2. Start two client, and send message.
     * 3. Stop one server which is connected by client1.
     * 4. Wait for client1 reconnect.
     * 5. Send message again.
     *
     * @throws Exception
     */
    @Test
    public void testReconnect() throws Exception {
        super.startTwoServer();
        super.startRoute();

        String routeUrl = "http://localhost:8083";
        String cj = "cj";
        String zs = "zs";
        Long cjId = super.registerAccount(cj);
        Long zsId = super.registerAccount(zs);
        var auth1 = ClientConfigurationData.Auth.builder()
                .userName(cj)
                .userId(cjId)
                .build();
        var auth2 = ClientConfigurationData.Auth.builder()
                .userName(zs)
                .userId(zsId)
                .build();
        var backoffStrategy = new RandomBackoff();

        @Cleanup
        Client client1 = Client.builder()
                .auth(auth1)
                .routeUrl(routeUrl)
                .backoffStrategy(backoffStrategy)
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state = client1.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));


        AtomicReference<String> client2Receive = new AtomicReference<>();
        @Cleanup
        Client client2 = Client.builder()
                .auth(auth2)
                .routeUrl(routeUrl)
                .messageListener((client, properties, message) -> client2Receive.set(message))
                .backoffStrategy(backoffStrategy)
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state2 = client2.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state2));

        Optional<CIMServerResVO> serverInfo2 = client2.getServerInfo();
        Assertions.assertTrue(serverInfo2.isPresent());
        System.out.println("client2 serverInfo = " + serverInfo2.get());

        // send msg
        String msg = "hello";
        client1.sendGroup(msg);
        Awaitility.await()
                .untilAsserted(() -> Assertions.assertEquals(msg, client2Receive.get()));
        client2Receive.set("");


        System.out.println("ready to restart server");
        TimeUnit.SECONDS.sleep(3);
        Optional<CIMServerResVO> serverInfo = client1.getServerInfo();
        Assertions.assertTrue(serverInfo.isPresent());
        System.out.println("server info = " + serverInfo.get());

        super.stopServer(serverInfo.get().getCimServerPort());
        System.out.println("stop server success! " + serverInfo.get());


        // Waiting server stopped, and client reconnect.
        TimeUnit.SECONDS.sleep(30);
        System.out.println("reconnect state: " + client1.getState());
        Awaitility.await().atMost(15, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));
        serverInfo = client1.getServerInfo();
        Assertions.assertTrue(serverInfo.isPresent());
        System.out.println("client1 reconnect server info = " + serverInfo.get());

        // Send message again.
        log.info("send message again, client2Receive = {}", client2Receive.get());
        client1.sendGroup(msg);
        Awaitility.await()
                .untilAsserted(() -> Assertions.assertEquals(msg, client2Receive.get()));
        super.stopTwoServer();
    }

    @Test
    public void offLineAndOnline() throws Exception {
        super.starSingleServer();
        super.startRoute();
        String routeUrl = "http://localhost:8083";
        String cj = "crossoverJie";
        String zs = "zs";
        Long id = super.registerAccount(cj);
        Long zsId = super.registerAccount(zs);
        var auth1 = ClientConfigurationData.Auth.builder()
                .userId(id)
                .userName(cj)
                .build();
        var auth2 = ClientConfigurationData.Auth.builder()
                .userId(zsId)
                .userName(zs)
                .build();

        @Cleanup
        Client client1 = Client.builder()
                .auth(auth1)
                .routeUrl(routeUrl)
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state = client1.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));
        Optional<CIMServerResVO> serverInfo = client1.getServerInfo();
        Assertions.assertTrue(serverInfo.isPresent());
        System.out.println("client1 serverInfo = " + serverInfo.get());


        AtomicReference<String> client2Receive = new AtomicReference<>();
        Client client2 = Client.builder()
                .auth(auth2)
                .routeUrl(routeUrl)
                .messageListener((client, properties, message) -> client2Receive.set(message))
                // Avoid auto reconnect, this test will manually close client.
                .reconnectCheck((client) -> false)
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state2 = client2.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state2));

        Optional<CIMServerResVO> serverInfo2 = client2.getServerInfo();
        Assertions.assertTrue(serverInfo2.isPresent());
        System.out.println("client2 serverInfo = " + serverInfo2.get());

        // send msg
        String msg = "hello";
        client1.sendGroup(msg);
        Awaitility.await().untilAsserted(
                () -> Assertions.assertEquals(msg, client2Receive.get()));
        client2Receive.set("");

        // Manually offline
        client2.close();
        TimeUnit.SECONDS.sleep(10);
        client2 = Client.builder()
                .auth(auth2)
                .routeUrl(routeUrl)
                .messageListener((client, properties, message) -> client2Receive.set(message))
                // Avoid to auto reconnect, this test will manually close client.
                .reconnectCheck((client) -> false)
                .build();
        ClientState.State state3 = client2.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state3));

        // send msg again
        client1.sendGroup(msg);
        Awaitility.await().untilAsserted(
                () -> Assertions.assertEquals(msg, client2Receive.get()));

        super.stopSingle();
    }

    @Test
    public void testClose() throws Exception {
        super.starSingleServer();
        super.startRoute();
        String routeUrl = "http://localhost:8083";
        String cj = "crossoverJie";
        Long id = super.registerAccount(cj);
        var auth1 = ClientConfigurationData.Auth.builder()
                .userId(id)
                .userName(cj)
                .build();

        Client client1 = Client.builder()
                .auth(auth1)
                .routeUrl(routeUrl)
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state = client1.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));
        Optional<CIMServerResVO> serverInfo = client1.getServerInfo();
        Assertions.assertTrue(serverInfo.isPresent());
        System.out.println("client1 serverInfo = " + serverInfo.get());

        client1.close();
        ClientState.State state1 = client1.getState();
        Assertions.assertEquals(ClientState.State.Closed, state1);
        super.stopSingle();
    }

    @Test
    public void testIncorrectUser() throws Exception {
        super.starSingleServer();
        super.startRoute();
        String routeUrl = "http://localhost:8083";
        String cj = "xx";
        long id = 100L;
        var auth1 = ClientConfigurationData.Auth.builder()
                .userId(id)
                .userName(cj)
                .build();

        Client client1 = Client.builder()
                .auth(auth1)
                .routeUrl(routeUrl)
                .build();
        TimeUnit.SECONDS.sleep(3);

        Assertions.assertDoesNotThrow(client1::close);

        super.stopSingle();
    }
}