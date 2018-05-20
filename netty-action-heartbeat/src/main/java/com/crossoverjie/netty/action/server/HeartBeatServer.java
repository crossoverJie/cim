package com.crossoverjie.netty.action.server;

import com.crossoverjie.netty.action.channel.init.HeartbeatInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 21/05/2018 00:30
 * @since JDK 1.8
 */
@Configuration
public class HeartBeatServer {

    @Bean
    public ChannelFuture buildFuture() {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(11211))
                    .childHandler(new HeartbeatInitializer());

            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
            return future;
        } catch (InterruptedException e) {
        } finally {
        }

        return null;
    }
}
