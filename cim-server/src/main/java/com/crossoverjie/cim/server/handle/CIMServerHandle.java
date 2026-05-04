package com.crossoverjie.cim.server.handle;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import com.crossoverjie.cim.server.kit.ReadReceiptHandler;
import com.crossoverjie.cim.server.kit.RouteHandler;
import com.crossoverjie.cim.server.kit.ServerHeartBeatHandlerImpl;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class CIMServerHandle extends SimpleChannelInboundHandler<Request> {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        CIMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
        if (userInfo != null) {
            log.warn("[{}] trigger channelInactive offline!", userInfo.getUserName());

            RouteHandler routeHandler = SpringBeanFactory.getBean(RouteHandler.class);
            routeHandler.userOffLine(userInfo, (NioSocketChannel) ctx.channel());

            ctx.channel().close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {

                log.info("!!READER_IDLE!!");

                HeartBeatHandler heartBeatHandler = SpringBeanFactory.getBean(ServerHeartBeatHandlerImpl.class);
                heartBeatHandler.process(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        log.info("received msg=[{}]", msg.toString());

        if (msg.getCmd() == BaseCommand.LOGIN_REQUEST) {
            SessionSocketHolder.put(msg.getRequestId(), (NioSocketChannel) ctx.channel());
            SessionSocketHolder.saveSession(msg.getRequestId(), msg.getReqMsg());
            log.info("client [{}] online success!!", msg.getReqMsg());
        }

        if (msg.getCmd() == BaseCommand.PING) {
            NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());
            Request heartBeat = SpringBeanFactory.getBean("heartBeat", Request.class);
            ctx.writeAndFlush(heartBeat).addListeners((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("IO error,close Channel");
                    future.channel().close();
                }
            });
        }

        if (msg.getCmd() == BaseCommand.P2P_MSG_READ) {
            ReadReceiptHandler readReceiptHandler = SpringBeanFactory.getBean(ReadReceiptHandler.class);
            readReceiptHandler.handleP2PMsgRead(msg);
            log.info("Handled P2P_MSG_READ command for userId={}", msg.getRequestId());
        }

        if (msg.getCmd() == BaseCommand.CONVERSATION_READ) {
            ReadReceiptHandler readReceiptHandler = SpringBeanFactory.getBean(ReadReceiptHandler.class);
            readReceiptHandler.handleConversationRead(msg);
            log.info("Handled CONVERSATION_READ command for userId={}", msg.getRequestId());
        }

        if (msg.getCmd() == BaseCommand.GROUP_MSG_READ) {
            ReadReceiptHandler readReceiptHandler = SpringBeanFactory.getBean(ReadReceiptHandler.class);
            readReceiptHandler.handleGroupMsgRead(msg);
            log.info("Handled GROUP_MSG_READ command for userId={}", msg.getRequestId());
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (CIMException.isResetByPeer(cause.getMessage())) {
            return;
        }

        log.error(cause.getMessage(), cause);

    }

}
