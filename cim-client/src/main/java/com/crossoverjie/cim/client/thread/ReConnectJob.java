package com.crossoverjie.cim.client.thread;

import com.crossoverjie.cim.client.service.impl.ClientHeartBeatHandlerImpl;
import com.crossoverjie.cim.client.util.SpringBeanFactory;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 21:35
 * @since JDK 1.8
 */
@Slf4j
public class ReConnectJob implements Runnable {


    private ChannelHandlerContext context ;

    private HeartBeatHandler heartBeatHandler ;

    public ReConnectJob(ChannelHandlerContext context) {
        this.context = context;
        this.heartBeatHandler = SpringBeanFactory.getBean(ClientHeartBeatHandlerImpl.class) ;
    }

    @Override
    public void run() {
        try {
            heartBeatHandler.process(context);
        } catch (Exception e) {
            log.error("Exception",e);
        }
    }
}
