package com.crossoverjie.cim.persistence.redis.constant;

/**
 * @author zhongcanyu
 * @date 2025/6/14
 * @description
 */
public final class Constant {

    /**
     * The number of messages captured from redis each time
     */
    public final static Integer FETCH_OFFLINE_MSG_SIZE = 100;

    /**
     * Default expire time for offline message
     */
    public final static Integer OFFLINE_MSG_TTL_DAYS = 7;

    /**
     * Redis key prefix for offline message
     */
    public static final String MSG_KEY = "offline:msg:";

    /**
     * Redis key prefix for offline message index
     */
    public static final String USER_IDX = "offline:msg:user:";
}
