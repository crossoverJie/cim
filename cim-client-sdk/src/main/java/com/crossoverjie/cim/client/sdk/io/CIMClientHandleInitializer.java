package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.common.handler.ChannelInboundDebugHandler;
import com.crossoverjie.cim.common.handler.ChannelOutboundDebugHandler;
import com.crossoverjie.cim.common.protocol.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CIMClientHandleInitializer extends ChannelInitializer<Channel> {

    private final boolean debug;

    private final ClientConfigurationData.Auth auth;

    public CIMClientHandleInitializer(boolean debug, ClientConfigurationData.Auth auth) {
        super();
        this.auth = auth;
        this.debug = debug;
    }

    @Override
    protected void initChannel(Channel ch) {
        final ChannelPipeline pip = ch.pipeline();
        pip.addLast(new IdleStateHandler(0, 10, 0));

        // decoder
        if (debug) {
            pip.addLast(ChannelInboundDebugHandler.INSTANCE);
        }
        pip.addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(Response.getDefaultInstance()));

        // encoder
        if (debug) {
            pip.addLast(ChannelOutboundDebugHandler.INSTANCE);
        }
        pip.addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new CIMClientHandle(auth));
    }
}
