package com.crossoverjie.cim.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/24 01:43
 * @since JDK 1.8
 */
@Component
public class AppConfiguration {

    @Value("${cim.user.id}")
    private Long userId;

    @Value("${cim.user.userName}")
    private String userName;

    @Value("${cim.msg.logger.path}")
    private String msgLoggerPath ;

    @Value("${cim.clear.route.request.url}")
    private String clearRouteUrl ;

    @Value("${cim.heartbeat.time}")
    private long heartBeatTime ;

    @Value("${cim.reconnect.count}")
    private int errorCount ;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsgLoggerPath() {
        return msgLoggerPath;
    }

    public void setMsgLoggerPath(String msgLoggerPath) {
        this.msgLoggerPath = msgLoggerPath;
    }


    public long getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }


    public String getClearRouteUrl() {
        return clearRouteUrl;
    }

    public void setClearRouteUrl(String clearRouteUrl) {
        this.clearRouteUrl = clearRouteUrl;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
