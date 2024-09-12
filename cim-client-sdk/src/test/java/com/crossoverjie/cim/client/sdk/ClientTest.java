package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.route.AbstractRouteBaseTest;
import java.util.concurrent.TimeUnit;
import lombok.Cleanup;
import org.awaitility.Awaitility;
import org.junit.Assert;
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
    public void createClient() throws Exception {
        super.startServer();
        super.startRoute();
        String routeUrl = "http://localhost:8083";
        String userName = "crossoverJie";
        Long id = super.registerAccount(userName);
        @Cleanup
        Client client = Client.builder()
                .userName(userName)
                .userId(id)
                .routeUrl(routeUrl)
                .build();
        ClientState.State state = client.getState();
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));

    }
}