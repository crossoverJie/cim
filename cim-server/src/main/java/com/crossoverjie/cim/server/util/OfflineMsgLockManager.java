package com.crossoverjie.cim.server.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class OfflineMsgLockManager {

    private final ConcurrentHashMap<Long, ReadWriteLock> userLocks = new ConcurrentHashMap<>();

    public void withWriteLock(Long userId, Runnable task) {
        ReadWriteLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            task.run();
        } finally {
            lock.writeLock().unlock();
            // 可选：在确定不再需要锁时移除，避免内存泄漏（需业务逻辑配合）
            // userLocks.remove(userId, lock);
        }
    }

    public void withReadLock(Long userId, Runnable task) {
        ReadWriteLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantReadWriteLock());
        lock.readLock().lock();
        try {
            task.run();
        } finally {
            lock.readLock().unlock();
        }
    }
}
