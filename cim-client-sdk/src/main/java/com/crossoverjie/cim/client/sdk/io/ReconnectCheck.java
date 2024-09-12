package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.client.sdk.Client;

public interface ReconnectCheck {

    /**
     * By the default, the client will reconnect to the server when the connection is close(inactive).
     * @return false if the client should not reconnect to the server.
     */
    boolean isNeedReconnect(Client client);
}
