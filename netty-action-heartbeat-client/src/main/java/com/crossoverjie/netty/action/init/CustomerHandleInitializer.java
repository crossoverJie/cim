package com.crossoverjie.netty.action.init;

import com.crossoverjie.netty.action.encode.HeartbeatEncode;
import com.crossoverjie.netty.action.handle.EchoClientHandle;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 23/02/2018 22:47
 * @since JDK 1.8
 */
public class CustomerHandleInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //10 秒没发送消息
                .addLast(new IdleStateHandler(0, 10, 0))
                .addLast(new HeartbeatEncode())
                .addLast(new EchoClientHandle())
        ;
    }
}
