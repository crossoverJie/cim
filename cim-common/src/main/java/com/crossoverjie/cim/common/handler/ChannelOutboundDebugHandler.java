package com.crossoverjie.cim.common.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenqwwq
 * @date 2025/6/14
 **/
@Slf4j
@ChannelHandler.Sharable
public class ChannelOutboundDebugHandler extends ChannelOutboundHandlerAdapter {

    public static final ChannelOutboundDebugHandler INSTANCE = new ChannelOutboundDebugHandler();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        if (msg instanceof ByteBuf) {
//            ByteBuf buf = (ByteBuf) msg;
//            String hexDump = ByteBufUtil.hexDump(buf);
//            log.info("16 进制报文内容: {}", hexDump);
            // 然后尝试用protoc解析
//        }
        super.write(ctx, msg, promise);
    }
}
