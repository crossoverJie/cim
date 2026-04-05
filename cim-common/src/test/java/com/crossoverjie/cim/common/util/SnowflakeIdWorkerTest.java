package com.crossoverjie.cim.common.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Function: 测试雪花算法
 * Date: 01/04/2026 20:00
 */
public class SnowflakeIdWorkerTest {

    /**
     * 测试雪花算法
     */
    @Test
    public void testSnowflakeIdWorker() {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker();
        Set<Long> ids = new HashSet<>();
        int count = 100000;

        for (int i = 0; i < count; i++) {
            long id = idWorker.nextId();
            assertTrue(ids.add(id),"Duplicate ID generated " + id);
        }
        assertEquals(count,ids.size());
    }

    /**
     * 测试雪花算法并发
     */
    @Test
    public void testNextIdConcurrent() throws InterruptedException {
        final int THREAD_COUNT = 100;
        final int IDS_PER_THREAD = 1000;
        final SnowflakeIdWorker idWorker = new SnowflakeIdWorker();
        final Set<Long> ids = Collections.synchronizedSet(new HashSet<>());
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        // 启动线程
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.execute(() -> {
                try {
                    for (int j = 0; j < IDS_PER_THREAD; j++) {
                        long id = idWorker.nextId();
                        assertTrue(ids.add(id), "Duplicate ID generated " + id);
                    }
                }  finally {

                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();
        assertEquals(THREAD_COUNT * IDS_PER_THREAD, ids.size());

    }

    /**
     * 测试雪花算法时间戳递增
     */
    @Test
    public void testNextIdMonotonicallyIncreasing() {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker();
        long lastId = 0;

        for (int i = 0; i < 100; i++) {
            long currentId = idWorker.nextId();
            assertTrue(currentId > lastId, "IDs should be monotonically increasing");
            lastId = currentId;
        }
    }
}
