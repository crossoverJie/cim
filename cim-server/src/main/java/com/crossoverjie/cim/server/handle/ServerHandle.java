package com.crossoverjie.cim.server.handle;

import com.crossoverjie.cim.common.protocol.BaseRequestProto;
import com.crossoverjie.cim.common.protocol.BaseResponseProto;
import com.crossoverjie.cim.server.util.NettySocketHolder;
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
public class ServerHandle extends SimpleChannelInboundHandler<BaseRequestProto.RequestProtocol> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHandle.class);


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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("有客户端连上来了。。");
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
