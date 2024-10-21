package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.sdk.io.MessageListener;
import com.crossoverjie.cim.client.service.MsgLogger;
import com.crossoverjie.cim.common.constant.Constants;
import java.util.Map;

/**
 * Function:自定义收到消息回调
 *
 * @author crossoverJie
 * Date: 2019/1/6 17:49
 * @since JDK 1.8
 */
public class MsgCallBackListener implements MessageListener {


    private final MsgLogger msgLogger;
    private final Event event;

    public MsgCallBackListener(MsgLogger msgLogger, Event event) {
        this.msgLogger = msgLogger;
        this.event = event;
    }


    @Override
    public void received(Client client, Map<String, String> properties, String msg) {
        String sendUserName = properties.getOrDefault(Constants.MetaKey.USER_NAME, "nobody");
        this.msgLogger.log(sendUserName + ":" + msg);
        this.event.info(sendUserName + ":" + msg);
    }
}
