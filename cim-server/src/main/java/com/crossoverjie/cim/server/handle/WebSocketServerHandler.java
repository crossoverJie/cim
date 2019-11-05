package com.crossoverjie.cim.server.handle;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.req.WebSocketRequest;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import com.crossoverjie.cim.server.util.SeesionWebSocketHolder;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;


@Component
@Sharable
public class WebSocketServerHandler extends CimSimpleChannelInboundHandler<Object> {


    /**
     * 描述：读取完连接的消息后，对消息进行处理。
     *      这里主要是处理WebSocket请求
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }
    
    /**
     * 描述：处理WebSocketFrame
     * @param ctx
     * @param frame
     * @throws Exception
     */
    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // 关闭请求
        if (frame instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker handshaker =
                    SeesionWebSocketHolder.getWebSocketServerHandshaker(ctx.channel().id().asLongText());
            if (handshaker == null) {
                sendMessage(ctx, "不存在的客户端连接！");
            } else {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        // ping请求
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //pong
        if(frame instanceof PongWebSocketFrame){
            return ;
        }
        // 只支持文本格式，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            sendMessage(ctx, "仅支持文本(Text)格式，不支持二进制消息");
            return;
        }

        // 客服端发送过来的消息
        String request = ((TextWebSocketFrame)frame).text();
        LOGGER.info("服务端收到新信息：" + request);
        WebSocketRequest msg = null;
        try {
            msg = JSONObject.parseObject(request, WebSocketRequest.class);
        } catch (Exception e) {
            sendMessage(ctx,"格式不正确");
            return;
        }

        if (msg.getType() == Constants.CommandType.LOGIN) {
            //保存客户端与 Channel 之间的关系
            SeesionWebSocketHolder.put(msg.getRequestId(), (NioSocketChannel) ctx.channel());
            SeesionWebSocketHolder.saveSession(msg.getRequestId(), msg.getReqMsg());
            LOGGER.info("客户端[{}]上线成功", msg.getReqMsg());
            return;
        }

        if (msg.getType() == Constants.CommandType.PING) {
            NettyAttrUtil.updateReaderTime(ctx.channel(),System.currentTimeMillis());
            //向客户端响应 pong 消息
            WebSocketRequest webSocketPing = SpringBeanFactory.getBean("heartBeatWs", WebSocketRequest.class);
            String pongJson = JSONObject.toJSONString(webSocketPing);
            ctx.writeAndFlush(new TextWebSocketFrame(pongJson)).addListeners((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    LOGGER.error("IO error,close Channel");
                    future.channel().close();
                }
            }) ;
            return;
        }

        //业务逻辑
//        sendMessage(ctx,"收到消息");
    }
    
    /**
     * 描述：客户端断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        CIMUserInfo userInfo = SeesionWebSocketHolder.getUserId((NioSocketChannel) ctx.channel());
        if (userInfo != null){
            LOGGER.warn("[{}]触发 channelInactive 掉线!",userInfo.getUserName());
            userOffLine(userInfo, (NioSocketChannel) ctx.channel());
            ctx.channel().close();
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //交给ServerHeartBeatHandlerImpl处理
        }
        super.userEventTriggered(ctx, evt);
    }
    /**
     * 异常处理：关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
    
    private void sendMessage(ChannelHandlerContext ctx, String msg) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame(msg));
    }

}
