package com.crossoverjie.cim.client.config;

import lombok.Data;
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
@Data
public class AppConfiguration {

    @Value("${cim.user.id}")
    private Long userId;

    @Value("${cim.user.userName}")
    private String userName;

    @Value("${cim.msg.logger.path}")
    private String msgLoggerPath ;

    @Value("${cim.heartbeat.time}")
    private long heartBeatTime ;

    @Value("${cim.reconnect.count}")
    private int reconnectCount;

    @Value("${cim.route.url}")
    private String routeUrl;
    @Value("${cim.callback.thread.queue.size}")
    private int queueSize;
    @Value("${cim.callback.thread.pool.size}")
    private int poolSize;
}
