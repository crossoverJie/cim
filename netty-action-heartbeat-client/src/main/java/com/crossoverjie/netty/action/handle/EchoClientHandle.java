package com.crossoverjie.netty.action.handle;

import com.crossoverjie.netty.action.common.pojo.CustomProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 16/02/2018 18:09
 * @since JDK 1.8
 */
public class EchoClientHandle extends SimpleChannelInboundHandler<ByteBuf> {

    private final static Logger LOGGER = LoggerFactory.getLogger(EchoClientHandle.class);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;

            if (idleStateEvent.state() == IdleState.WRITER_IDLE){
                LOGGER.info("已经 10 秒没有发送信息！");
                //向客户端发送消息
                CustomProtocol customProtocol = new CustomProtocol(45678L,"ping") ;
                ctx.writeAndFlush(Unpooled.copiedBuffer(customProtocol.toString(), CharsetUtil.UTF_8)) ;
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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in) throws Exception {

        //从服务端收到消息时被调用
        LOGGER.info("客户端收到消息={}",in.toString(CharsetUtil.UTF_8)) ;

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常时断开连接
        cause.printStackTrace() ;
        ctx.close() ;
    }
}
