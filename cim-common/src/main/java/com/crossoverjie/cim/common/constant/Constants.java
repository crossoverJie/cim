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
    public static final String COUNTER_SERVER_PUSH_COUNT = "counter.server.push.count";


    /**
     * 客户端手动 push 次数
     */
    public static final String COUNTER_CLIENT_PUSH_COUNT = "counter.client.push.count";

    public static class MetaKey {
        public static final String SEND_USER_ID = "sendUserId";
        public static final String SEND_USER_NAME = "sendUserName";
        public static final String RECEIVE_USER_ID = "receiveUserId";
        public static final String RECEIVE_USER_NAME = "receiveUserName";
        public static final String MESSAGE_ID = "messageId";
        public static final String GROUP_ID = "groupId";
        public static final String CONVERSATION_ID = "conversationId";
        public static final String CONVERSATION_TYPE = "conversationType";
        public static final String MESSAGE_TYPE = "messageType";
        public static final String READER_USER_ID = "readerUserId";
        public static final String READER_USER_NAME = "readerUserName";
        public static final String READ_COUNT = "readCount";
        public static final String TOTAL_MEMBERS = "totalMembers";
        public static final String READ_TYPE = "readType";
        public static final String READ_AT = "readAt";
        public static final String BEFORE_MESSAGE_ID = "beforeMessageId";
        public static final String GROUP_MEMBER_IDS = "groupMemberIds";
    }

    //从数据库读取离线消息的每次获取量
    public static final Integer FETCH_OFFLINE_MSG_LIMIT = 100;

    public static final Integer OFFLINE_MSG_PENDING = 0;

    public static final Integer OFFLINE_MSG_DELIVERED = 1;

    public static final Integer MSG_NOT_READ = 0;

    public static final Integer MSG_HAS_READ = 1;

    public static final Integer MSG_TYPE_TEXT = 0;

    public static final Integer MSG_TYPE_IMAGE = 1;

    public static final Integer MSG_TYPE_VOICE = 2;

    public static final Integer MSG_TYPE_VIDEO = 3;

    public static final Integer MSG_TYPE_FILE = 4;

    public static final Integer CONVERSATION_TYPE_P2P = 0;

    public static final Integer CONVERSATION_TYPE_GROUP = 1;

    public static final Integer READ_TYPE_SINGLE = 1;

    public static final Integer READ_TYPE_CONVERSATION = 2;

    public static final Integer OFFLINE_MSG_EXPIRE_DAYS = 7;

    public static final String CONVERSATION_ID_SEPARATOR = "_";

}
