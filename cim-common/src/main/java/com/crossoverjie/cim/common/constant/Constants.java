package com.crossoverjie.cim.common.constant;

/**
 * Function:常量
 *
 * @author crossoverJie
 *         Date: 28/03/2018 17:41
 * @since JDK 1.8
 */
public class Constants {


    /**
     * 服务端手动 push 次数
     */
    public static final String COUNTER_SERVER_PUSH_COUNT = "counter.server.push.count" ;


    /**
     * 客户端手动 push 次数
     */
    public static final String COUNTER_CLIENT_PUSH_COUNT = "counter.client.push.count" ;


    /**
     * 自定义报文类型
     */
    public static class CommandType{
        /**
         * 登录
         */
        public static final int LOGIN = 1 ;
        /**
         * 业务消息
         */
        public static final int MSG = 2 ;

        /**
         * ping
         */
        public static final int PING = 3 ;
    }


}
