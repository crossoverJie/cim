package com.crossoverjie.netty.action.handle;

import com.crossoverjie.netty.action.common.pojo.CustomProtocol;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
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
 *         Date: 17/05/2018 18:52
 * @since JDK 1.8
 */
public class HeartBeatSimpleHandle extends SimpleChannelInboundHandler<CustomProtocol> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;

            if (idleStateEvent.state() == IdleState.READER_IDLE){
                LOGGER.info("已经5秒没有收到信息！");
                //向客户端发送消息
                CustomProtocol customProtocol = new CustomProtocol(12345L,"pong") ;
                ctx.writeAndFlush(Unpooled.copiedBuffer(customProtocol.toString(), CharsetUtil.UTF_8))
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE) ;
            }


        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomProtocol customProtocol) throws Exception {
        LOGGER.info("customProtocol={}", customProtocol);

        //手动处理数据并返回
        customProtocol.setHeader(customProtocol.getHeader() + 1000);
        customProtocol.setContent(customProtocol.getContent() + "asdfg");
        ctx.writeAndFlush(Unpooled.copiedBuffer(customProtocol.toString(), CharsetUtil.UTF_8));
    }
}
