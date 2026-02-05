package com.crossoverjie.cim.route.constant;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/9/10 14:07
 * @since JDK 1.8
 */
public final class Constant {


    /**
     * 账号前缀
     */
    public static final String ACCOUNT_PREFIX = "cim-account:";

    /**
     * 路由信息前缀
     */
    public static final String ROUTE_PREFIX = "cim-route:";

    /**
     * 登录状态前缀
     */
    public static final String LOGIN_STATUS_PREFIX = "login-status";


    public static final class OfflineStoreMode {
        /**
         * redis
         */
        public static final String REDIS = "redis";

        /**
         * mysql
         */
        public static final String MYSQL = "mysql";
    }


}
