package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.client.sdk.impl.ClientImpl;
import com.crossoverjie.cim.client.sdk.io.MessageListener;
import com.crossoverjie.cim.client.sdk.io.backoff.RandomBackoff;
import com.crossoverjie.cim.client.sdk.route.AbstractRouteBaseTest;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.constant.Constant;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ClientTest extends AbstractRouteBaseTest {


    @AfterEach
    public void tearDown() {
        super.close();
    }

    @Test
    public void testClientAuthCanRead() throws Exception {
        // 启动 ZK 和连接服务器
        super.starSingleServer();

        // 启动转发服务
        super.startRoute(Constant.OfflineStoreMode.REDIS);

        // 转发服务地址
        String routeUrl = "http://localhost:8083";

        // 登录第一个用户
        String cj = "crossoverJie";
        Long id = super.registerAccount(cj);
        var auth1 = ClientConfigurationData.Auth.builder()
                .userId(id)
                .userName(cj)
                .build();

        @Cleanup
        Client client1 = Client.builder()
                .auth(auth1)
                .routeUrl(routeUrl)     // routeUrl 也可以用于登录获取连接服务器地址
                .build();
        TimeUnit.SECONDS.sleep(3);
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, client1.getState()));
        Optional<CIMServerResVO> serverInfo = client1.getServerInfo();
        Assertions.assertTrue(serverInfo.isPresent());
        System.out.println("client1 serverInfo = " + serverInfo.get());

        String msg = "hello";
        client1.sendGroup(msg);
        super.stopSingle();
        client1.close();;
    }

    @Test
    public void groupChat() throws Exception {
        super.starSingleServer();
        super.startRoute(Constant.OfflineStoreMode.REDIS);
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
                    Assertions.assertEquals(properties.get(Constants.MetaKey.SEND_USER_ID), String.valueOf(auth1.getUserId()));
                    Assertions.assertEquals(properties.get(Constants.MetaKey.SEND_USER_NAME), auth1.getUserName());
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
        client1.close();
        client2.close();
    }

    @Test
    public void testP2PChat() throws Exception {
        super.starSingleServer();
        super.startRoute(Constant.OfflineStoreMode.REDIS);
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

        // client1 send msg to client3
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

        // client2 send batch msg to client1
        var batchMsg = List.of("a", "b", "c");
        client2.sendP2P(P2PReqVO.builder()
                .receiveUserId(cjId)
                .batchMsg(batchMsg)
                .build());

        Assertions.assertEquals(ClientImpl.getClientMap().size(), 3);
        Awaitility.await().untilAsserted(
                () -> Assertions.assertEquals(msg, client3Receive.get()));
        Awaitility.await().untilAsserted(
                () -> Assertions.assertNull(client2Receive.get()));
        Awaitility.await().untilAsserted(
                () -> Assertions.assertEquals(batchMsg, client1Receive));
        super.stopSingle();
        client1.close();
        client2.close();
        client3.close();
        Assertions.assertEquals(ClientImpl.getClientMap().size(), 0);
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
        // 启动两个连接服务器
        super.startTwoServer();

        // 启动路由服务器
        super.startRoute(Constant.OfflineStoreMode.REDIS);

        // 注册两个账号
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

        // 建立两个客户端
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
                .messageListener(new MessageListener() {
                    @Override
                    public void received(Client client, Map<String, String> properties, String msg) {
                        System.out.println("|| ================  收到消息:" + msg);
                        client2Receive.set(msg);
                    }
                })
                .backoffStrategy(backoffStrategy)
                .build();
        TimeUnit.SECONDS.sleep(3);

        // 两个客户端连接成功
        ClientState.State state2 = client2.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state2));

        Optional<CIMServerResVO> serverInfo2 = client2.getServerInfo();
        Assertions.assertTrue(serverInfo2.isPresent());
        System.out.println("client2 serverInfo = " + serverInfo2.get());

        // 发送消息并验证
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

        // 关闭连接的服务
        super.stopServer(serverInfo.get().getCimServerPort());
        System.out.println("stop server success! " + serverInfo.get());


        // Waiting server stopped, and client reconnect.
        // 应该会重连到另外一个服务
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
        client1.close();
        client2.close();
    }

    @Test
    public void offLineAndOnline() throws Exception {
        super.starSingleServer();
        super.startRoute(Constant.OfflineStoreMode.REDIS);
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
        client1.close();
        client2.close();
    }

    @Test
    public void testClose() throws Exception {
        super.starSingleServer();
        super.startRoute(Constant.OfflineStoreMode.REDIS);
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
        super.startRoute(Constant.OfflineStoreMode.REDIS);
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