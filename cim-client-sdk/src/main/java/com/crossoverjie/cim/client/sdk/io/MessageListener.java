package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.client.sdk.Client;

public interface MessageListener {

    /**
     * @param client client
     * @param msg msgs
     */
    void received(Client client, String msg);
}
