package com.crossoverjie.cim.persistence.api.util;

import org.springframework.stereotype.Component;

/**
 * @author zhongcanyu
 * @date 2025/5/18
 * @description
 */
@Component
public class SnowflakeIdWorker {
    private final long workerId = 1L;
    private final static long EPOCH = 1622505600000L;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private final static long WORKER_ID_BITS = 10L;
    private final static long SEQUENCE_BITS = 12L;
    private final static long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private final static long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    private long tilNextMillis(long lastTs) {
        long ts = System.currentTimeMillis();
        while (ts <= lastTs) {
            ts = System.currentTimeMillis();
        }
        return ts;
    }

    public synchronized long nextId() {
        long ts = System.currentTimeMillis();
        if (ts < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards.");
        }
        if (ts == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                ts = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = ts;
        return ((ts - EPOCH) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }
}
