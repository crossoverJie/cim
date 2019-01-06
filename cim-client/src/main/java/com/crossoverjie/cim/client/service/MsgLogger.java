package com.crossoverjie.cim.client.service;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2019/1/6 15:23
 * @since JDK 1.8
 */
public interface MsgLogger {

    /**
     * 异步写入消息
     * @param msg
     */
    void log(String msg) ;


    /**
     * 停止写入
     */
    void stop() ;

    /**
     * 查询聊天记录
     * @param key 关键字
     * @return
     */
    String query(String key) ;
}
