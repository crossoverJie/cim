package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientImpl;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class ReConnectManager {

    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Trigger reconnect job
     *
     * @param ctx
     */
    public void reConnect(ChannelHandlerContext ctx) {
        buildExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
                    try {
                        ClientImpl.getClient().getHeartBeatHandler().process(ctx);
                    } catch (Exception e) {
                        ClientImpl.getClient().getConf().getEvent().error("ReConnectManager reConnect error", e);
                    }
                },
                0, 10, TimeUnit.SECONDS);
    }

    /**
     * Close reconnect job if reconnect success.
     */
    public void reConnectSuccess() {
        scheduledExecutorService.shutdown();
    }


    /***
     * build a thread executor
     */
    private void buildExecutor() {
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            ThreadFactory factory = new ThreadFactoryBuilder()
                    .setNameFormat("reConnect-job-%d")
                    .setDaemon(true)
                    .build();
            scheduledExecutorService = new ScheduledThreadPoolExecutor(1, factory);
        }
    }

    private static class ClientHeartBeatHandle implements HeartBeatHandler {

        @Override
        public void process(ChannelHandlerContext ctx) throws Exception {
            ClientImpl.getClient().reconnect();
        }
    }

    public static ReConnectManager createReConnectManager() {
        return new ReConnectManager();
    }

    public static HeartBeatHandler createHeartBeatHandler() {
        return new ClientHeartBeatHandle();
    }
}
