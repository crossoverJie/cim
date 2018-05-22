package com.crossoverjie.netty.action.util;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 18:33
 * @since JDK 1.8
 */
public class NettySocketHolder {
    private static final Map<Long,NioSocketChannel> MAP = new ConcurrentHashMap<>(16) ;

    public static void put(Long id,NioSocketChannel socketChannel){
        MAP.put(id,socketChannel) ;
    }

    public static NioSocketChannel get(Long id) {
        return MAP.get(id);
    }
}
