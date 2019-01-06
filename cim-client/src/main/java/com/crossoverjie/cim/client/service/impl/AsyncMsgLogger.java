package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.service.MsgLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2019/1/6 15:26
 * @since JDK 1.8
 */
@Service
public class AsyncMsgLogger implements MsgLogger {

    private final static Logger LOGGER = LoggerFactory.getLogger(AsyncMsgLogger.class);

    /**
     * The default buffer size.
     */
    private static final int DEFAULT_QUEUE_SIZE = 16;
    private BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(DEFAULT_QUEUE_SIZE);

    private volatile boolean started = false ;
    private Worker worker = new Worker() ;


    @Override
    public void log(String msg) {
        //开始消费
        startMsgLogger();
        try {
            blockingQueue.put(msg);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException", e);
        }
    }

    private class Worker extends Thread {


        @Override
        public void run() {
            while (started) {
                try {
                    String msg = blockingQueue.take();
                    LOGGER.info("写入聊天记录={}", msg);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }

    /**
     * 开始工作
     */
    private void startMsgLogger(){
        if (started){
            return ;
        }

        worker.setDaemon(true);
        worker.setName("AsyncMsgLogger-Worker");
        started = true ;
        worker.start();
    }


    @Override
    public void stop() {
        started = false ;
        worker.interrupt();
    }
}
