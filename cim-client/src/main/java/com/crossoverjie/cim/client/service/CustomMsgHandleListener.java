package com.crossoverjie.cim.client.service;

/**
 * Function: 自定义消息回调
 *
 * @author crossoverJie
 *         Date: 2018/12/26 17:24
 * @since JDK 1.8
 */
public interface CustomMsgHandleListener {

    /**
     * 消息回调
     * @param msg
     */
    void handle(String msg);
}
