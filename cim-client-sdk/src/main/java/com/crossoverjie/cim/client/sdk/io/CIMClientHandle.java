package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.client.sdk.ClientState;
import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.client.sdk.impl.ClientImpl;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.enums.ChannelAttributeKeys;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.common.protocol.Response;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.common.StringUtils;

import java.util.Objects;

@ChannelHandler.Sharable
@Slf4j
public class CIMClientHandle extends SimpleChannelInboundHandler<Response> {


    private final ClientConfigurationData.Auth auth;

    public CIMClientHandle(ClientConfigurationData.Auth auth) {
        this.auth = auth;
    }

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

        // 获取认证信息
        // channelActive 执行时间过早,所以这些属性没办法放在 channel 上
        final String token = auth.getAuthToken();
        if (StringUtils.isBlank(token)) {
            log.error("auth token is blank!");
            ctx.close();
            return;
        }
        final long userId = auth.getUserId();

        // 连接建立之后就发送认证请求
        Request authReq = Request.newBuilder()
                .setRequestId(userId)
                .setCmd(BaseCommand.LOGIN_REQUEST)
                .setReqMsg(token)
                .build();

        ctx.writeAndFlush(authReq).syncUninterruptibly().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("auth msg send success,userId:{},userName:{}", auth.getUserId(), auth.getUserName());
                    ClientImpl.getClient().getConf().getEvent().debug("ChannelActive");
                    ctx.channel().attr(ChannelAttributeKeys.USER_ID).set(userId);
                    log.info("channel is active,userId:{}", userId);
                    ClientImpl.getClient().setState(ClientState.State.Ready);
                } else {
                    log.error("auth msg send failure,userId:{},userName:{}", auth.getUserId(), auth.getUserName());
                    ctx.channel().close();  // 认证失败关闭连接
                }
            }
        });


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
            ClientImpl client;
            if ((Objects.isNull(receiveUserId) || ((client = ClientImpl.getClientMap().get(Long.valueOf(receiveUserId))) == null))) {
                log.error("client not found for userId: {}", receiveUserId);
                return;
            }
            // callback
            client.getConf().getCallbackThreadPool().execute(() -> {
                log.info("client address: {} :{}", ctx.channel().remoteAddress(), client);
                MessageListener messageListener = client.getConf().getMessageListener();
                if (msg.getBatchResMsgCount() > 0) {
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
