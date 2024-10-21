package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.client.sdk.Client;
import java.util.Map;

public interface MessageListener {

    /**
     * @param client     client
     * @param properties meta data
     * @param msg        msgs
     */
    void received(Client client, Map<String, String> properties, String msg);
}
