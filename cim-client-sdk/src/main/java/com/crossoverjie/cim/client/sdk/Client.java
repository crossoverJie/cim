package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientBuilderImpl;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface Client extends Closeable {

    static ClientBuilder builder() {
        return new ClientBuilderImpl();
    }

    void send(String msg) throws Exception;

    // TODO: 2024/9/12 messageId
    CompletableFuture<Void> sendAync(String msg);

    ClientState.State getState();

    Long getUserId();
    
}
