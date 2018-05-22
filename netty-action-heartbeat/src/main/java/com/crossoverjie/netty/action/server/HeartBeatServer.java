package com.crossoverjie.netty.action.server;

import com.crossoverjie.netty.action.Application;
import com.crossoverjie.netty.action.channel.init.HeartbeatInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 21/05/2018 00:30
 * @since JDK 1.8
 */
@Component
public class HeartBeatServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatServer.class);

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();


    /**
     * 启动 Netty
     * @return
     * @throws InterruptedException
     */
    @PostConstruct
    public ChannelFuture start() throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(11211))
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new HeartbeatInitializer());

        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()){
            LOGGER.info("启动 Netty 成功");
        }

        return future;
    }


    /**
     * 销毁
     */
    @PreDestroy
    public void destroy(){
        boss.shutdownGracefully().syncUninterruptibly() ;
        work.shutdownGracefully().syncUninterruptibly() ;
        LOGGER.info("关闭 Netty 成功");
    }
}
