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

    public static class MetaKey {
        public static final String SEND_USER_ID = "sendUserId" ;
        public static final String SEND_USER_NAME = "sendUserName" ;
        public static final String RECEIVE_USER_ID = "receiveUserId" ;
        public static final String RECEIVE_USER_NAME = "receiveUserName" ;
    }

    //从数据库读取离线消息的每次获取量
    public static final Integer FETCH_OFFLINE_MSG_LIMIT = 100 ;

    public static final Integer OFFLINE_MSG_PENDING = 0 ;

    public static final Integer OFFLINE_MSG_DELIVERED = 1 ;

    public static final Integer MSG_TYPE_TEXT = 0 ;

    public static final Integer MSG_TYPE_IMAGE = 1 ;

}
