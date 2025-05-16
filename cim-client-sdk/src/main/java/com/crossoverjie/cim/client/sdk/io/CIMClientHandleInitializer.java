package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.common.protocol.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

public class CIMClientHandleInitializer extends ChannelInitializer<Channel> {

    private final CIMClientHandle cimClientHandle = new CIMClientHandle();

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                .addLast(new IdleStateHandler(0, 10, 0))

                // google Protobuf
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(Response.getDefaultInstance()))
                //处理只打印出4条日志的问题
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(cimClientHandle)
        ;
    }
}
