package com.crossoverjie.cim.server.init;

import com.crossoverjie.cim.common.protocol.BaseRequestProto;
import com.crossoverjie.cim.server.handle.ServerHandle;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 17/05/2018 18:51
 * @since JDK 1.8
 */
public class HeartbeatInitializer extends ChannelInitializer<Channel> {

    private final ServerHandle serverHandle = new ServerHandle();

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ch.pipeline()
                // google Protobuf 编解码
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(BaseRequestProto.RequestProtocol.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(serverHandle);
    }
}
