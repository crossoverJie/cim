package com.crossoverjie.netty.action.client.channel.init;

import com.crossoverjie.netty.action.client.handle.HeartBeatSimpleHandle;
import com.crossoverjie.netty.action.client.decoder.HeartbeatDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 17/05/2018 18:51
 * @since JDK 1.8
 */
public class HeartbeatInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //五秒没有收到消息
                .addLast(new IdleStateHandler(5, 0, 0))
                .addLast(new HeartbeatDecoder())
                .addLast(new HeartBeatSimpleHandle());
    }
}
