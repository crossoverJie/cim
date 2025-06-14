package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.client.sdk.ClientState;
import com.crossoverjie.cim.client.sdk.impl.ClientImpl;
import com.crossoverjie.cim.common.constant.Constants;
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

        // 发送认证报文
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
            String receiveUserId = msg.getPropertiesMap().get(Constants.MetaKey.RECEIVE_USER_ID);
            ClientImpl client = ClientImpl.getClientMap().get(Long.valueOf(receiveUserId));
            if (client == null) {
                log.error("client not found for userId: {}", receiveUserId);
                return;
            }
            // callback
            client.getConf().getCallbackThreadPool().execute(() -> {
                log.info("client address: {} :{}", ctx.channel().remoteAddress(), client);
                MessageListener messageListener = client.getConf().getMessageListener();
                if (msg.getBatchResMsgCount() >0 ){
                    for (int i = 0; i < msg.getBatchResMsgCount(); i++) {
                        messageListener.received(client, msg.getPropertiesMap(), msg.getBatchResMsg(i));
                    }
                } else {
                    messageListener.received(client, msg.getPropertiesMap(), msg.getResMsg());
                }
            });
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ClientImpl.getClient().getConf().getEvent().error(cause.getCause().toString());
        ctx.close();
    }
}
