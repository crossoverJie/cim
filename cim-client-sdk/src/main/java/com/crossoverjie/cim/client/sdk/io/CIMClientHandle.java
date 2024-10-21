package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.client.sdk.ClientState;
import com.crossoverjie.cim.client.sdk.impl.ClientImpl;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Response;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class CIMClientHandle extends SimpleChannelInboundHandler<Response> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent idleStateEvent) {

            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {

                ctx.writeAndFlush(ClientImpl.getClient().getHeartBeatPacket()).addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.error("heart beat error,close Channel");
                        ClientImpl.getClient().getConf().getEvent().warn("heart beat error,close Channel");
                        future.channel().close();
                    }
                });
            }

        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ClientImpl.getClient().getConf().getEvent().debug("ChannelActive");
        ClientImpl.getClient().setState(ClientState.State.Ready);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        if (!ClientImpl.getClient().getConf().getReconnectCheck().isNeedReconnect(ClientImpl.getClient())) {
            return;
        }
        ClientImpl.getClient().setState(ClientState.State.Closed);

        ClientImpl.getClient().getConf().getEvent().warn("Client inactive, let's reconnect");
        ClientImpl.getClient().getReConnectManager().reConnect(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) {


        if (msg.getCmd() == BaseCommand.PING) {
            ClientImpl.getClient().getConf().getEvent().debug("received ping from server");
            NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());
        }

        if (msg.getCmd() != BaseCommand.PING) {
            // callback
            ClientImpl.getClient().getConf().getCallbackThreadPool().execute(() -> {
                ClientImpl.getClient().getConf().getMessageListener().received(ClientImpl.getClient(), msg.getPropertiesMap(), msg.getResMsg());
            });
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ClientImpl.getClient().getConf().getEvent().error(cause.getCause().toString());
        ctx.close();
    }
}
