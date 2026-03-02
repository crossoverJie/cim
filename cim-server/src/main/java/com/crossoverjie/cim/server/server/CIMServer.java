package com.crossoverjie.cim.server.server;

import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.config.OtelConfig;
import com.crossoverjie.cim.server.init.CIMServerInitializer;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.InetSocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 21/05/2018 00:30
 * @since JDK 1.8
 */
@Component
@Slf4j
public class CIMServer {

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();

    @Autowired
    private Tracer tracer;

    @Value("${cim.server.port}")
    private int nettyPort;

    /**
     * 启动 cim server
     *
     * @return
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(nettyPort))
                // 保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new CIMServerInitializer());

        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            log.info("Start cim server success!!!");
        }
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        boss.shutdownGracefully().syncUninterruptibly();
        work.shutdownGracefully().syncUninterruptibly();
        log.info("Close cim server success!!!");
    }

    /**
     * Push msg to client.
     *
     * @param sendMsgReqVO message body
     */
    public void sendMsg(SendMsgReqVO sendMsgReqVO) {
        NioSocketChannel socketChannel = SessionSocketHolder.get(sendMsgReqVO.getUserId());

        if (null == socketChannel) {
            log.error("client {} offline!", sendMsgReqVO.getUserId());
            return;
        }

        // === OpenTelemetry: 创建消息推送 Span ===
        Span span = tracer != null
                ? tracer.spanBuilder("cim.server.pushMsg").startSpan()
                : null;

        try {
            Request.Builder requestBuilder = Request.newBuilder()
                    .setRequestId(sendMsgReqVO.getUserId())
                    .putAllProperties(sendMsgReqVO.getProperties())
                    .setCmd(sendMsgReqVO.getCmd());

            boolean isBatch = sendMsgReqVO.getBatchMsg() != null && sendMsgReqVO.getBatchMsg().size() > 0;
            if (isBatch) {
                requestBuilder.addAllBatchReqMsg(sendMsgReqVO.getBatchMsg());
            } else {
                requestBuilder.setReqMsg(sendMsgReqVO.getMsg());
            }

            Request protocol = requestBuilder.build();

            ChannelFuture future = socketChannel.writeAndFlush(protocol);
            future.addListener(
                    (ChannelFutureListener) channelFuture -> log.info("server push {} msg:[{}], socketChannel:{}",
                            isBatch ? "batch" : "single", sendMsgReqVO, socketChannel));

            // 在 Span 中记录推送目标信息
            if (span != null) {
                span.setAttribute("targetUserId", sendMsgReqVO.getUserId());
                span.setAttribute("messageType", isBatch ? "batch" : "single");
                span.setStatus(StatusCode.OK);
            }

            // === Metrics: 递增消息推送计数器 ===
            if (OtelConfig.getMessagePushedCounter() != null) {
                OtelConfig.getMessagePushedCounter().increment();
            }
        } catch (Exception e) {
            if (span != null) {
                span.setStatus(StatusCode.ERROR, e.getMessage());
                span.recordException(e);
            }
            throw e;
        } finally {
            if (span != null) {
                span.end();
            }
        }
    }
}
