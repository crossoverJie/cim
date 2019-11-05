package com.crossoverjie.cim.server.init;

import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import com.crossoverjie.cim.server.handle.CIMServerHandle;
import com.crossoverjie.cim.server.handle.HttpRequestHandler;
import com.crossoverjie.cim.server.handle.WebSocketServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 17/05/2018 18:51
 * @since JDK 1.8
 */
public class CIMServerInitializer extends ChannelInitializer<Channel> {

    private final CIMServerHandle cimServerHandle = new CIMServerHandle();

    @Override
    protected void initChannel(Channel ch) throws Exception {

        ch.pipeline()
                //11 秒没有向客户端发送消息就发生心跳
                .addLast(new IdleStateHandler(11, 0, 0))

                //websocket部分
                .addLast("http-codec", new HttpServerCodec()) // HTTP编码解码器
                .addLast("aggregator", new HttpObjectAggregator(65536))// 把HTTP头、HTTP体拼成完整的HTTP请求
                .addLast("http-chunked", new ChunkedWriteHandler()) // 方便大文件传输，不过实质上都是短的文本数据
                .addLast("http-handler", new HttpRequestHandler())
                .addLast("websocket-handler", new WebSocketServerHandler())

                // google Protobuf 编解码
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(CIMRequestProto.CIMReqProtocol.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(cimServerHandle);


    }
}
