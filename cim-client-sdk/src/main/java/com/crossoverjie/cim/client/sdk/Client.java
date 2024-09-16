package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientBuilderImpl;
import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import java.io.Closeable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface Client extends Closeable {

    static ClientBuilder builder() {
        return new ClientBuilderImpl();
    }

    default void sendP2P(P2PReqVO p2PReqVO) throws Exception{
        sendP2PAsync(p2PReqVO).get();
    };

    CompletableFuture<Void> sendP2PAsync(P2PReqVO p2PReqVO);

    default void sendGroup(String msg) throws Exception{
        sendGroupAsync(msg).get();
    };

    CompletableFuture<Void> sendGroupAsync(String msg);

    ClientState.State getState();

    ClientConfigurationData.Auth getAuth();

    Set<CIMUserInfo> getOnlineUser() throws Exception;

    Optional<CIMServerResVO> getServerInfo();

}
