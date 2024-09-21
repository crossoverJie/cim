package com.crossoverjie.cim.client.sdk.io.backoff;

import java.util.concurrent.TimeUnit;

/**
 * @author:qjj
 * @create: 2024-09-21 12:16
 * @Description: backoff strategy interface
 */

public interface BackoffStrategy {
    /**
     * @return the backoff time in milliseconds
     */
    long nextBackoff();

    /**
     * Run the backoff strategy
     * @throws InterruptedException
     */
    default void runBackoff() throws InterruptedException {
        TimeUnit.SECONDS.sleep(nextBackoff());
    }
}
