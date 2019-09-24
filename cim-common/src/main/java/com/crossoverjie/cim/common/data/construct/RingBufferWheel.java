package com.crossoverjie.cim.common.data.construct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Function:Ring Queue, it can be used to delay task.
 *
 * @author crossoverJie
 * Date: 2019-09-20 14:46
 * @since JDK 1.8
 */
public final class RingBufferWheel {

    private Logger logger = LoggerFactory.getLogger(RingBufferWheel.class);


    private static final int STATIC_RING_SIZE = 64;

    private Object[] ringBuffer;

    private int bufferSize;

    private ExecutorService executorService;

    private AtomicInteger taskSize = new AtomicInteger();

    private volatile boolean stop = false;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public RingBufferWheel(ExecutorService executorService) {
        this.executorService = executorService;
        bufferSize = STATIC_RING_SIZE;
        ringBuffer = new Object[bufferSize];
    }


    public void addTask(Task task) {
        int key = task.getKey();
        Set<Task> tasks = get(key);

        if (tasks != null) {
            int cycleNum = cycleNum(key, bufferSize);
            task.setCycleNum(cycleNum);
            tasks.add(task);
        } else {
            int index = mod(key, bufferSize);
            int cycleNum = cycleNum(key, bufferSize);
            task.setCycleNum(index);
            task.setCycleNum(cycleNum);
            Set<Task> sets = new HashSet<>();
            sets.add(task);
            put(key, sets);
        }

        taskSize.incrementAndGet();

    }

    public int taskSize() {
        return taskSize.get();
    }

    public void start() {
        logger.info("delay task is starting");
        Thread job = new Thread(new TriggerJob());
        job.setName("consumer RingBuffer thread");
        job.start();
    }

    public void stop(boolean force) {
        if (force) {
            logger.info("delay task is forced stop");
            stop = true;
            executorService.shutdownNow();
        } else {
            logger.info("delay task is stopping");
            try {
                lock.lock();
                condition.await();
                stop = true;
            } catch (InterruptedException e) {
                logger.error("InterruptedException", e);
            } finally {
                lock.unlock();
            }
            executorService.shutdown();
        }


    }


    private Set<Task> get(int key) {
        int index = mod(key, bufferSize);
        return (Set<Task>) ringBuffer[index];
    }

    private void put(int key, Set<Task> tasks) {
        int index = mod(key, bufferSize);
        ringBuffer[index] = tasks;
    }

    private Set<Task> remove(int key) {
        Set<Task> tempTask = new HashSet<>();
        Set<Task> result = new HashSet<>();

        Set<Task> tasks = (Set<Task>) ringBuffer[key];
        if (tasks == null) {
            return result;
        }

        for (Task task : tasks) {
            if (task.getCycleNum() == 0) {
                result.add(task);

                size2Notify();
            } else {
                // decrement 1 cycle number and update origin data
                task.setCycleNum(task.getCycleNum() - 1);
                tempTask.add(task);
            }
        }

        //update origin data
        ringBuffer[key] = tempTask;

        return result;
    }

    private void size2Notify() {
        lock.lock();
        int size = taskSize.decrementAndGet();
        if (size == 0) {
            condition.signal();
        }
        lock.unlock();
    }

    private int mod(int target, int mod) {
        // equals target % mod
        return target & (mod - 1);
    }

    private int cycleNum(int target, int mod) {
        //equals target/mod
        return target >> Integer.bitCount(mod - 1);
    }

    public abstract static class Task extends Thread {


        private int cycleNum;

        private int key;

        @Override
        public void run() {
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public int getCycleNum() {
            return cycleNum;
        }

        private void setCycleNum(int cycleNum) {
            this.cycleNum = cycleNum;
        }
    }


    private class TriggerJob implements Runnable {

        @Override
        public void run() {
            int index = 0;
            while (!stop) {

                Set<Task> tasks = remove(index);
                for (Task task : tasks) {
                    executorService.submit(task);
                }

                if (++index > bufferSize - 1) {
                    index = 0;
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    logger.error("InterruptedException", e);
                }
            }

            logger.info("delay task is stopped");
        }
    }
}
