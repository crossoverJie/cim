package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.io.MessageListener;
import com.crossoverjie.cim.client.service.MsgLogger;

/**
 * Function:自定义收到消息回调
 *
 * @author crossoverJie
 * Date: 2019/1/6 17:49
 * @since JDK 1.8
 */
public class MsgCallBackListener implements MessageListener {


    private final MsgLogger msgLogger;

    public MsgCallBackListener(MsgLogger msgLogger) {
        this.msgLogger = msgLogger;
    }


    @Override
    public void received(Client client, String msg) {
        msgLogger.log(msg);
    }
}
