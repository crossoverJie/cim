package com.crossoverjie.netty.action.client.handle;

import com.crossoverjie.netty.action.client.util.SpringBeanFactory;
import com.crossoverjie.netty.action.common.pojo.CustomProtocol;
import com.crossoverjie.netty.action.common.protocol.BaseRequestProto;
import com.crossoverjie.netty.action.common.protocol.BaseResponseProto;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 16/02/2018 18:09
 * @since JDK 1.8
 */
public class EchoClientHandle extends SimpleChannelInboundHandler<BaseResponseProto.ResponseProtocol> {

    private final static Logger LOGGER = LoggerFactory.getLogger(EchoClientHandle.class);




    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;

            if (idleStateEvent.state() == IdleState.WRITER_IDLE){
                LOGGER.info("已经 10 秒没有发送信息！");
                //向服务端发送消息
                BaseRequestProto.RequestProtocol heartBeat = SpringBeanFactory.getBean("heartBeat", BaseRequestProto.RequestProtocol.class);
                ctx.writeAndFlush(heartBeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE) ;
            }


        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //客户端和服务端建立连接时调用
        LOGGER.info("已经建立了联系。。");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, BaseResponseProto.ResponseProtocol responseProtocol) throws Exception {

        //从服务端收到消息时被调用
        //LOGGER.info("客户端收到消息={}",in.toString(CharsetUtil.UTF_8)) ;

        LOGGER.info("客户端收到消息={}" ,responseProtocol.getResMsg());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常时断开连接
        cause.printStackTrace() ;
        ctx.close() ;
    }
}
