package com.crossoverjie.netty.action.client.encode;

import com.crossoverjie.netty.action.common.pojo.CustomProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Function:编码
 *
 * @author crossoverJie
 *         Date: 17/05/2018 19:07
 * @since JDK 1.8
 */
public class HeartbeatEncode extends MessageToByteEncoder<CustomProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CustomProtocol msg, ByteBuf out) throws Exception {

        out.writeLong(msg.getId()) ;
        out.writeBytes(msg.getContent().getBytes()) ;

    }
}
