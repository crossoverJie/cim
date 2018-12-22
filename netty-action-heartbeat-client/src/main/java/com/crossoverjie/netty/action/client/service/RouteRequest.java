package com.crossoverjie.netty.action.client.service;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/22 22:26
 * @since JDK 1.8
 */
public interface RouteRequest {

    /**
     * 群发消息
     * @param msg 消息
     * @throws Exception
     */
    void sendGroupMsg(String msg) throws Exception;
}
