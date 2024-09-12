package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientBuilderImpl;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public interface Client extends Closeable {

    static ClientBuilder builder() {
        return new ClientBuilderImpl();
    }

    void sendGroup(String msg) throws Exception;

    CompletableFuture<Void> sendGroupeAsync(String msg);

    ClientState.State getState();

    Long getUserId();

}
