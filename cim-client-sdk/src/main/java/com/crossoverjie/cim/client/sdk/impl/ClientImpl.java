package com.crossoverjie.cim.client.sdk.impl;

import static com.crossoverjie.cim.common.enums.StatusEnum.RECONNECT_FAIL;

import com.crossoverjie.cim.client.sdk.*;
import com.crossoverjie.cim.client.sdk.io.CIMClientHandleInitializer;
import com.crossoverjie.cim.common.data.construct.RingBufferWheel;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
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

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;
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
    private final HeartBeatHandler heartBeatHandler = ReConnectManager.createHeartBeatHandler();
    @Getter
    private final ReConnectManager reConnectManager = ReConnectManager.createReConnectManager();

    @Getter
    private static ClientImpl client;
    @Getter
    private static Map<Long, ClientImpl> clientMap = new ConcurrentHashMap<>();
    @Getter
    private final Request heartBeatPacket;

    // Client connected server info
    private CIMServerResVO serverInfo;

    public ClientImpl(ClientConfigurationData conf) {
        this.conf = conf;

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

        heartBeatPacket = Request.newBuilder()
                .setRequestId(this.conf.getAuth().getUserId())
                .setReqMsg("ping")
                .setCmd(BaseCommand.PING)
                .build();
        client = this;
        clientMap.put(conf.getAuth().getUserId(), this);

        connectServer(v -> this.conf.getEvent().info("Login success!"));

        postConnectionSetup();
    }

    /**
     * 1. Pull offline messages from the server
     */
    private void postConnectionSetup() {
        new RingBufferWheel(Executors.newFixedThreadPool(1))
                .addTask(new FetchOfflineMsgJob(routeManager, conf));
    }

    private void connectServer(Consumer<Void> success) {
        this.doConnectServer().whenComplete((r, e) -> {
            if (r) {
                success.accept(null);
            }
            if (e != null) {
                if (e instanceof CIMException cimException && cimException.getErrorCode()
                        .equals(RECONNECT_FAIL.getCode())) {
                    this.conf.getEvent().fatal(this);
                } else {
                    if (errorCount++ >= this.conf.getLoginRetryCount()) {
                        this.conf.getEvent()
                                .error("The maximum number of reconnections has been reached[{}]times, exit cim client!",
                                        errorCount);
                        this.conf.getEvent().fatal(this);
                    }
                }
            }

        });
    }

    /**
     * 1. User login and get target server
     * 2. Connect target server
     * 3. send login cmd to server
     */
    private CompletableFuture<Boolean> doConnectServer() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        this.userLogin(future).ifPresentOrElse((cimServer) -> {
            this.doConnectServer(cimServer, future);
            this.loginServer();
            this.serverInfo = cimServer;
            future.complete(true);
        }, () -> {
            this.conf.getEvent().error("Login fail!, cannot get server info!");
            this.conf.getEvent().fatal(this);
            future.complete(false);
        });
        return future;
    }

    /**
     * Login and get server info
     *
     * @return Server info
     */
    private Optional<CIMServerResVO> userLogin(CompletableFuture<Boolean> future) {
        LoginReqVO loginReqVO = new LoginReqVO(conf.getAuth().getUserId(),
                conf.getAuth().getUserName());

        CIMServerResVO cimServer = null;
        try {
            cimServer = routeManager.getServer(loginReqVO);
            log.info("cimServer=[{}]", cimServer);
        } catch (Exception e) {
            log.error("login fail", e);
            future.completeExceptionally(e);
        }
        return Optional.ofNullable(cimServer);
    }

    private final EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("cim-work"));

    private void doConnectServer(CIMServerResVO cimServer, CompletableFuture<Boolean> future) {
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
            future.completeExceptionally(e);
        }
    }

    /**
     * Send login cmd to server
     */
    private void loginServer() {
        Request login = Request.newBuilder()
                .setRequestId(this.conf.getAuth().getUserId())
                .setReqMsg(this.conf.getAuth().getUserName())
                .setCmd(BaseCommand.LOGIN_REQUEST)
                .build();
        channel.writeAndFlush(login)
                .addListener((ChannelFutureListener) channelFuture ->
                        this.conf.getEvent().info("Registry cim server success!")
                );
    }

    /**
     * 1. clear route information.
     * 2. reconnect.
     * 3. shutdown reconnect job.
     * 4. reset reconnect state.
     * @throws Exception
     */
    public void reconnect() throws Exception {
        if (channel != null && channel.isActive()) {
            return;
        }
        this.serverInfo = null;
        // clear route information.
        this.routeManager.offLine(this.getConf().getAuth().getUserId());

        this.conf.getEvent().info("cim trigger reconnecting....");

        this.conf.getBackoffStrategy().runBackoff();

        // don't set State ready, because when connect success, the State will be set to ready automate.
        connectServer(v -> {
            this.reConnectManager.reConnectSuccess();
            this.conf.getEvent().info("Great! reConnect success!!!");
        });
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
            channel = null;
        }
        super.setState(ClientState.State.Closed);
        this.routeManager.offLine(this.getAuth().getUserId());
        this.clientMap.remove(this.getAuth().getUserId());
    }

    @Override
    public CompletableFuture<Void> sendP2PAsync(P2PReqVO p2PReqVO) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        p2PReqVO.setUserId(this.conf.getAuth().getUserId());
        return routeManager.sendP2P(future, p2PReqVO);
    }

    @Override
    public CompletableFuture<Void> sendGroupAsync(String msg) {
        // TODO: 2024/9/12 return messageId
        return this.routeManager.sendGroupMsg(new ChatReqVO(this.conf.getAuth().getUserId(), msg, null));
    }

    @Override
    public ClientConfigurationData.Auth getAuth() {
        return this.conf.getAuth();
    }

    @Override
    public ClientState.State getState() {
        return super.getState();
    }

    @Override
    public Set<CIMUserInfo> getOnlineUser() throws Exception {
        return routeManager.onlineUser();
    }

    @Override
    public Optional<CIMServerResVO> getServerInfo() {
        return Optional.ofNullable(this.serverInfo);
    }
}
