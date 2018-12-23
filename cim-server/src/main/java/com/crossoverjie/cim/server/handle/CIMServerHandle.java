package com.crossoverjie.cim.server.handle;

import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import com.crossoverjie.cim.common.protocol.CIMResponseProto;
import com.crossoverjie.cim.server.util.NettySocketHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 17/05/2018 18:52
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
public class CIMServerHandle extends SimpleChannelInboundHandler<CIMRequestProto.CIMReqProtocol> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CIMServerHandle.class);


    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端断开");
        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("有客户端连上来了。。");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CIMRequestProto.CIMReqProtocol msg) throws Exception {
        LOGGER.info("收到msg={}", msg.getReqMsg());

        if (999 == msg.getRequestId()){
            CIMResponseProto.CIMResProtocol responseProtocol = CIMResponseProto.CIMResProtocol.newBuilder()
                    .setResponseId(1000)
                    .setResMsg("服务端响应")
                    .build();
            ctx.writeAndFlush(responseProtocol) ;
        }

        //保存客户端与 Channel 之间的关系
        NettySocketHolder.put((long) msg.getRequestId(),(NioSocketChannel)ctx.channel()) ;
    }
}
