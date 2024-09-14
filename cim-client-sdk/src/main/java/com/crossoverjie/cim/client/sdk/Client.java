package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientBuilderImpl;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import java.io.Closeable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface Client extends Closeable {

    static ClientBuilder builder() {
        return new ClientBuilderImpl();
    }

    void sendGroup(String msg) throws Exception;

    CompletableFuture<Void> sendGroupeAsync(String msg);

    ClientState.State getState();

    Long getUserId();

    Set<CIMUserInfo> getOnlineUser() throws Exception;

    Optional<CIMServerResVO> getServerInfo();

}
