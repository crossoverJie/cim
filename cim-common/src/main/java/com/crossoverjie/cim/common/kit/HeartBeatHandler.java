package com.crossoverjie.cim.common.kit;

import io.netty.channel.ChannelHandlerContext;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 17:15
 * @since JDK 1.8
 */
public interface HeartBeatHandler {

    /**
     * 处理心跳
     * @param ctx
     * @throws Exception
     */
    void process(ChannelHandlerContext ctx) throws Exception ;
}
