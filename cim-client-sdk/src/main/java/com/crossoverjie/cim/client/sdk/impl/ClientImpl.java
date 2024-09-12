package com.crossoverjie.cim.client.sdk.impl;

import static com.crossoverjie.cim.common.enums.StatusEnum.RECONNECT_FAIL;
import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.ClientState;
import com.crossoverjie.cim.client.sdk.RouteManager;
import com.crossoverjie.cim.client.sdk.io.CIMClientHandleInitializer;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import com.crossoverjie.cim.common.util.StringUtil;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientImpl extends ClientState implements Client {

    @Getter
    private final ClientConfigurationData conf;
    private static final int CALLBACK_QUEUE_SIZE = 1024;
    private static final int CALLBACK_POOL_SIZE = 10;

    // ======= private ========
    private int errorCount;
    private SocketChannel channel;

    private final RouteManager routeManager;

    @Getter
    private static ClientImpl client;
    @Getter
    private final CIMRequestProto.CIMReqProtocol heartBeat;

    public ClientImpl(ClientConfigurationData conf) {
        this.conf = conf;
        if (this.conf.getUserId() <= 0 || StringUtil.isEmpty(this.conf.getUserName())) {
            throw new IllegalArgumentException("userId and userName must be set");
        }

        if (StringUtil.isEmpty(this.conf.getRouteUrl())) {
            throw new IllegalArgumentException("routeUrl must be set");
        }
        if (this.conf.getCallbackThreadPool() == null) {
            BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(CALLBACK_QUEUE_SIZE);
            ThreadFactory factory = new ThreadFactoryBuilder()
                    .setNameFormat("msg-callback-%d")
                    .setDaemon(true)
                    .build();
            this.conf.setCallbackThreadPool(
                    new ThreadPoolExecutor(CALLBACK_POOL_SIZE, CALLBACK_POOL_SIZE, 1, TimeUnit.SECONDS, queue,
                            factory));
        }

        routeManager = new RouteManager(conf.getRouteUrl(), conf.getOkHttpClient(), conf.getEvent());

        heartBeat = CIMRequestProto.CIMReqProtocol.newBuilder()
                .setRequestId(this.conf.getUserId())
                .setReqMsg("ping")
                .setType(Constants.CommandType.PING)
                .build();
        client = this;

        this.userLogin().ifPresentOrElse((cimServer) -> {
            this.connectServer(cimServer);
            this.loginServer();
        }, () -> {
            this.conf.getEvent().error("login fail");
            this.conf.getEvent().fatal(this);
        });
    }

    private Optional<CIMServerResVO> userLogin() {
        LoginReqVO loginReqVO = new LoginReqVO(conf.getUserId(),
                conf.getUserName());

        CIMServerResVO cimServer = null;
        try {
            cimServer = routeManager.getServer(loginReqVO);
            log.info("cimServer=[{}]", cimServer);
        } catch (CIMException cimException) {
            if (cimException.getErrorCode().equals(RECONNECT_FAIL.getCode())) {
                this.conf.getEvent().fatal(this);
            }
        } catch (Exception e) {
            errorCount++;
            if (errorCount >= this.conf.getLoginRetryCount()) {
                this.conf.getEvent()
                        .warn("The maximum number of reconnections has been reached[{}]times, exit cim client!",
                                errorCount);
                this.conf.getEvent().fatal(this);
            }
            log.error("login fail", e);
        }
        return Optional.ofNullable(cimServer);
    }

    private final EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("cim-work"));

    private void connectServer(CIMServerResVO cimServer) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new CIMClientHandleInitializer());
        ChannelFuture sync;
        try {
            sync = bootstrap.connect(cimServer.getIp(), cimServer.getCimServerPort()).sync();
            if (sync.isSuccess()) {
                this.conf.getEvent().info("Start cim client success!");
                channel = (SocketChannel) sync.channel();
            }
        } catch (InterruptedException e) {
            errorCount++;
            if (errorCount >= this.conf.getLoginRetryCount()) {
                this.conf.getEvent()
                        .warn("The maximum number of reconnections has been reached[{}]times, exit cim client!",
                                errorCount);
                this.conf.getEvent().fatal(this);
            }
        }

    }

    /**
     * Send login cmd to server
     */
    private void loginServer() {
        CIMRequestProto.CIMReqProtocol login = CIMRequestProto.CIMReqProtocol.newBuilder()
                .setRequestId(this.conf.getUserId())
                .setReqMsg(this.conf.getUserName())
                .setType(Constants.CommandType.LOGIN)
                .build();
        channel.writeAndFlush(login)
                .addListener((ChannelFutureListener) channelFuture ->
                        this.conf.getEvent().info("Registry cim server success!")
                );
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public void sendGroup(String msg) throws Exception {
        sendGroupeAsync(msg).get();
    }

    @Override
    public CompletableFuture<Void> sendGroupeAsync(String msg) {
        // TODO: 2024/9/12 return messageId
        return this.routeManager.sendGroupMsg(new ChatReqVO(this.conf.getUserId(), msg));
    }

    @Override
    public Long getUserId() {
        return this.conf.getUserId();
    }

    @Override
    public ClientState.State getState() {
        return super.getState();
    }
}
