package com.crossoverjie.cim.common.data.construct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-10-09 22:48
 * @since JDK 1.8
 */
public class TimerTest {

    private static Logger logger = LoggerFactory.getLogger(TimerTest.class) ;

    public static void main(String[] args) {
        logger.info("start");
        Timer timer = new Timer() ;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("test");
            }
        },50000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("test");
            }
        },30000);

    }
}
