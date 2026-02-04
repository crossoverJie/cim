package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientBuilderImpl;
import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Client extends Closeable {

    Logger log = LoggerFactory.getLogger(Client.class);

    static ClientBuilder builder() {
        return new ClientBuilderImpl();
    }

    default void sendP2P(P2PReqVO p2PReqVO) throws Exception{
        recordSendLog(sendP2PAsync(p2PReqVO), "P2P");
    };

    CompletableFuture<Void> sendP2PAsync(P2PReqVO p2PReqVO);

    default void sendGroup(String msg) throws Exception{
        recordSendLog(sendGroupAsync(msg), "GROUP");

    };
    default void recordSendLog(CompletableFuture<Void> future, String msgWay){
        future.orTimeout(10, TimeUnit.SECONDS)
                .whenComplete((result, throwable) -> {
                    if (throwable == null) {
                        log.info(msgWay + " message task completed successfully");
                    } else if (throwable instanceof TimeoutException) {
                        log.error(msgWay + " message processing timeout", throwable);
                    } else {
                        log.warn(msgWay + " message task completed with exception", throwable);
                    }
                });
    }

    CompletableFuture<Void> sendGroupAsync(String msg);

    ClientState.State getState();

    ClientConfigurationData.Auth getAuth();

    Set<CIMUserInfo> getOnlineUser() throws Exception;

    Optional<CIMServerResVO> getServerInfo();

}
