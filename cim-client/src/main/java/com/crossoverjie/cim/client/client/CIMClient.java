package com.crossoverjie.cim.client.client;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.client.init.CIMClientHandleInitializer;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.GoogleProtocolVO;
import com.crossoverjie.cim.client.vo.res.CIMServerResVO;
import com.crossoverjie.cim.common.pojo.CustomProtocol;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 14:19
 * @since JDK 1.8
 */
@Component
public class CIMClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(CIMClient.class);

    private EventLoopGroup group = new NioEventLoopGroup();


    @Value("${netty.server.port}")
    private int nettyPort;

    @Value("${netty.server.host}")
    private String host;

    private SocketChannel channel;

    @Autowired
    private RouteRequest routeRequest ;

    @PostConstruct
    public void start() throws Exception {
        //获取可以使用的服务器 ip+port
        CIMServerResVO.ServerInfo cimServer = routeRequest.getCIMServer();
        LOGGER.info("cimServer=[{}]",cimServer.toString());

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new CIMClientHandleInitializer())
        ;

        ChannelFuture future = bootstrap.connect(cimServer.getIp(), cimServer.getPort()).sync();
        if (future.isSuccess()) {
            LOGGER.info("启动 cim client 成功");
        }
        channel = (SocketChannel) future.channel();
    }

    /**
     * 发送消息
     *
     * @param customProtocol
     */
    public void sendMsg(CustomProtocol customProtocol) {
        ChannelFuture future = channel.writeAndFlush(customProtocol);
        future.addListener((ChannelFutureListener) channelFuture ->
                LOGGER.info("客户端手动发消息成功={}", JSON.toJSONString(customProtocol)));

    }
    /**
     * 发送消息字符串
     *
     * @param msg
     */
    public void sendStringMsg(String msg) {
        ByteBuf message = Unpooled.buffer(msg.getBytes().length) ;
        message.writeBytes(msg.getBytes()) ;
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListener((ChannelFutureListener) channelFuture ->
                LOGGER.info("客户端手动发消息成功={}", msg));

    }

    /**
     * 发送 Google Protocol 编解码字符串
     *
     * @param googleProtocolVO
     */
    public void sendGoogleProtocolMsg(GoogleProtocolVO googleProtocolVO) {

        CIMRequestProto.CIMReqProtocol protocol = CIMRequestProto.CIMReqProtocol.newBuilder()
                .setRequestId(googleProtocolVO.getRequestId())
                .setReqMsg(googleProtocolVO.getMsg())
                .build();


        ChannelFuture future = channel.writeAndFlush(protocol);
        future.addListener((ChannelFutureListener) channelFuture ->
                LOGGER.info("客户端手动发送 Google Protocol 成功={}", googleProtocolVO.toString()));

    }
}
