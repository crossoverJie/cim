package com.crossoverjie.cim.common.data.construct;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RingBufferWheelTest {

    private static Logger logger = LoggerFactory.getLogger(RingBufferWheelTest.class) ;

    public static void main(String[] args) throws InterruptedException {
        test1();

        return;
    }

    private static void test1() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2) ;

        Task task = new Task() ;
        task.setKey(10);
        RingBufferWheel wheel = new RingBufferWheel(executorService) ;
        wheel.addTask(task) ;

        task = new Task() ;
        task.setKey(74);
        wheel.addTask(task) ;

        while (true){
            logger.info("task size={}" , wheel.taskSize());
            TimeUnit.SECONDS.sleep(1);
        }
    }
    private static void test2() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2) ;

        Task task = new Task() ;
        task.setKey(10);
        RingBufferWheel wheel = new RingBufferWheel(executorService) ;
        wheel.addTask(task) ;

        task = new Task() ;
        task.setKey(74);
        wheel.addTask(task) ;

        wheel.start();

//        new Thread(() -> {
//            while (true){
//                logger.info("task size={}" , wheel.taskSize());
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        TimeUnit.SECONDS.sleep(12);
        wheel.stop(true);


    }
    private static void test3() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2) ;

        Task task = new Task() ;
        task.setKey(10);
        RingBufferWheel wheel = new RingBufferWheel(executorService) ;
        wheel.addTask(task) ;

        task = new Task() ;
        task.setKey(74);
        wheel.addTask(task) ;

        wheel.start();


        TimeUnit.SECONDS.sleep(2);
        wheel.stop(false);


    }
    private static void test4() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2) ;

        RingBufferWheel wheel = new RingBufferWheel(executorService) ;

        for (int i = 0; i < 65; i++) {
            Job task = new Job(i) ;
            task.setKey(i);
            wheel.addTask(task);
        }

        wheel.start();

        logger.info("task size={}",wheel.taskSize());

        wheel.stop(false);


    }
    private static void test5() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2) ;

        RingBufferWheel wheel = new RingBufferWheel(executorService,512) ;

        for (int i = 0; i < 65; i++) {
            Job task = new Job(i) ;
            task.setKey(i);
            wheel.addTask(task);
        }

        logger.info("task size={}",wheel.taskSize());

        wheel.stop(false);


    }


    private static class Task extends RingBufferWheel.Task{

        @Override
        public void run() {
            logger.info("================");
        }
    }
    private static void cuncrrentTest6() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue(10);
        ThreadFactory product = new ThreadFactoryBuilder()
                .setNameFormat("msg-callback-%d")
                .setDaemon(true)
                .build();
        ThreadPoolExecutor business = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MILLISECONDS, queue,product);

        ExecutorService executorService = Executors.newFixedThreadPool(10) ;
        RingBufferWheel wheel = new RingBufferWheel(executorService) ;

        business.execute(new Runnable() {
            @Override
            public void run() {

            }
        });

        for (int i = 0; i < 10; i++) {
            RingBufferWheel.Task task = new Job(i) ;
            task.setKey(i);
            wheel.addTask(task);
        }


        TimeUnit.SECONDS.sleep(10);
        RingBufferWheel.Task task = new Job(15) ;
        task.setKey(15);
        wheel.addTask(task);

        logger.info("task size={}",wheel.taskSize());

        wheel.stop(false);


    }

    private static class Job extends RingBufferWheel.Task{

        private int num ;

        public Job(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            logger.info("number={}" , num);
        }
    }
}