package com.crossoverjie.cim.server.handle;

import com.crossoverjie.cim.common.protocol.BaseRequestProto;
import com.crossoverjie.cim.common.protocol.BaseResponseProto;
import com.crossoverjie.cim.server.util.NettySocketHolder;
import com.crossoverjie.cim.common.pojo.CustomProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
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
public class HeartBeatSimpleHandle extends SimpleChannelInboundHandler<BaseRequestProto.RequestProtocol> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);

    private static final ByteBuf HEART_BEAT =  Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(new CustomProtocol(123456L,"pong").toString(),CharsetUtil.UTF_8));


    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        /*if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt ;

            if (idleStateEvent.state() == IdleState.READER_IDLE){
                LOGGER.info("已经5秒没有收到信息！");
                //向客户端发送消息
                ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE) ;
            }


        }*/

        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseRequestProto.RequestProtocol msg) throws Exception {
        LOGGER.info("收到msg={}", msg.getReqMsg());

        if (999 == msg.getRequestId()){
            BaseResponseProto.ResponseProtocol responseProtocol = BaseResponseProto.ResponseProtocol.newBuilder()
                    .setResponseId(1000)
                    .setResMsg("服务端响应")
                    .build();
            ctx.writeAndFlush(responseProtocol) ;
        }

        //保存客户端与 Channel 之间的关系
        NettySocketHolder.put((long) msg.getRequestId(),(NioSocketChannel)ctx.channel()) ;
    }
}
