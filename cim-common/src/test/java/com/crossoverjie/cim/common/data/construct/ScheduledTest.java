package com.crossoverjie.cim.common.data.construct;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-10-11 10:41
 * @since JDK 1.8
 */
@Slf4j
public class ScheduledTest {

    public static void main(String[] args) {
        log.info("start.....");
        ThreadFactory scheduled = new ThreadFactoryBuilder()
                .setNameFormat("scheduled-%d")
                .build();
        ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(2, scheduled);
        scheduledExecutorService.schedule(() -> log.info("scheduled........."), 3, TimeUnit.SECONDS);
    }
}

