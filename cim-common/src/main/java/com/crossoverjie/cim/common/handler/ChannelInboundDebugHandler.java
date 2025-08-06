package com.crossoverjie.cim.common.handler;

import com.crossoverjie.cim.common.enums.ChannelAttributeKeys;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenqwwq
 * @date 2025/6/14
 **/
@Slf4j
@ChannelHandler.Sharable
public class ChannelInboundDebugHandler extends ChannelInboundHandlerAdapter {

    public static final ChannelInboundDebugHandler INSTANCE = new ChannelInboundDebugHandler();

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final Long userId = ctx.channel().attr(ChannelAttributeKeys.USER_ID).get();
        log.info("user id:{}, channel is inactive", userId);
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (msg instanceof ByteBuf) {
//            ByteBuf buf = (ByteBuf) msg;
//            String hexDump = ByteBufUtil.hexDump(buf);
//            log.info("16进制报文内容：{}", hexDump);
//             用protoc解析
//        }
        super.channelRead(ctx, msg);
    }
}
