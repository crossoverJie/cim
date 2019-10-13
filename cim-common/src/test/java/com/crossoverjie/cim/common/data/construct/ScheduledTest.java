package com.crossoverjie.cim.common.data.construct;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-10-11 10:41
 * @since JDK 1.8
 */
public class ScheduledTest {

    private static Logger logger = LoggerFactory.getLogger(ScheduledTest.class) ;

    public static void main(String[] args) {
        logger.info("start.....");
        ThreadFactory scheduled = new ThreadFactoryBuilder()
                .setNameFormat("scheduled-%d")
                .build();
        ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(2,scheduled) ;
        scheduledExecutorService.schedule(() -> logger.info("scheduled........."),3, TimeUnit.SECONDS) ;
    }
}
