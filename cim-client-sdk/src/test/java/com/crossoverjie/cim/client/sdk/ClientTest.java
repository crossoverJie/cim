package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.route.AbstractRouteBaseTest;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Cleanup;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClientTest extends AbstractRouteBaseTest {


    @Test
    public void testBuilder() {
    }

    @AfterEach
    public void tearDown() {
        super.close();
    }

    @Test
    public void groupChat() throws Exception {
        super.startServer();
        super.startRoute();
        String routeUrl = "http://localhost:8083";
        String c1 = "crossoverJie";
        String zs = "zs";
        Long id = super.registerAccount(c1);
        Long zsId = super.registerAccount(zs);

        @Cleanup
        Client client1 = Client.builder()
                .userName(c1)
                .userId(id)
                .routeUrl(routeUrl)
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state = client1.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));

        AtomicReference<String> client2Receive = new AtomicReference<>();
        @Cleanup
        Client client2 = Client.builder()
                .userName(zs)
                .userId(zsId)
                .routeUrl(routeUrl)
                .messageListener((client, message) -> client2Receive.set(message))
                .build();
        TimeUnit.SECONDS.sleep(3);
        ClientState.State state2 = client2.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state2));

        // send msg
        String msg = "hello";
        client1.sendGroup(msg);

        Awaitility.await().untilAsserted(() -> Assertions.assertEquals(String.format("crossoverJie:%s", msg), client2Receive.get()));;

    }
}