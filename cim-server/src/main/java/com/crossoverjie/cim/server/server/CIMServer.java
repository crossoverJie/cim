package com.crossoverjie.cim.server.server;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.server.api.vo.req.SaveOfflineMsgReqVO;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.decorator.OfflineStore;
import com.crossoverjie.cim.server.factory.OfflineMsgFactory;
import com.crossoverjie.cim.server.init.CIMServerInitializer;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import com.crossoverjie.cim.server.service.OfflineMsgService;
import com.crossoverjie.cim.server.service.RedisWALService;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.protobuf.ByteString;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 21/05/2018 00:30
 * @since JDK 1.8
 */
@Component
@Slf4j
public class CIMServer {


    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();


    @Value("${cim.server.port}")
    private int nettyPort;

    @Resource
    private RedisWALService redisWALService;

    @Resource
    private OfflineMsgService offlineMsgService;
    @Resource
    private OfflineMsgFactory offlineMsgFactory;
    @Autowired
    private OfflineStore offlineStore;

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
                //保持长连接
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
     * @param sendMsgReqVO 消息
     */
    public void sendMsg(SendMsgReqVO sendMsgReqVO) {
        NioSocketChannel socketChannel = SessionSocketHolder.get(sendMsgReqVO.getUserId());

        if (null == socketChannel) {
            log.error("client {} offline!", sendMsgReqVO.getUserId());
            return;
        }
        Request protocol = Request.newBuilder()
                .setRequestId(sendMsgReqVO.getUserId())
                .setReqMsg(sendMsgReqVO.getMsg())
                .putAllProperties(sendMsgReqVO.getProperties())
                .setCmd(BaseCommand.MESSAGE)
                .build();

        ChannelFuture future = socketChannel.writeAndFlush(protocol);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("server push msg:[{}]", sendMsgReqVO.toString()));
    }

    public void sendOfflineMsgs(Long userId) {
        NioSocketChannel channel = SessionSocketHolder.get(userId);
        if (channel == null) {
            log.error("client {} offline!", userId);
            return;
        }

        List<OfflineMsg> fetchMsgs = offlineStore.fetch(userId);
        if (fetchMsgs.isEmpty()) {
            return;
        }

        for (OfflineMsg offlineMsg : fetchMsgs) {
            Request protocol = Request.newBuilder()
                    .setRequestId(offlineMsg.getUserId())
                    .setReqMsg(String.valueOf(ByteString.copyFrom(offlineMsg.getContent())))  // 假设 content 是 byte[]
                    .putAllProperties(offlineMsg.getProperties())
                    .setCmd(BaseCommand.MESSAGE)
                    .build();
            channel.write(protocol);
        }

        ChannelFuture lastFuture = (ChannelFuture) channel.flush();
        lastFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                offlineStore.markDelivered(userId, fetchMsgs.stream().map(OfflineMsg::getMessageId).toList());
                log.info("server push {} msgs to user {}", fetchMsgs.size(), userId);
            } else {
                log.error("failed to push {} msgs to user {}",
                        fetchMsgs.size(), userId, future.cause());
            }
        });
    }



    public void saveOfflineMsg(SaveOfflineMsgReqVO vo) {
        OfflineMsg offlineMsg = offlineMsgFactory.createFromVo(vo);
        offlineStore.save(offlineMsg);
    }
}
