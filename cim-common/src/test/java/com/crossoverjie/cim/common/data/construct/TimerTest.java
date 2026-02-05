package com.crossoverjie.cim.common.data.construct;

import java.util.Timer;
import java.util.TimerTask;
import lombok.extern.slf4j.Slf4j;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-10-09 22:48
 * @since JDK 1.8
 */
@Slf4j
public class TimerTest {

    public static void main(String[] args) {
        log.info("start");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("test");
            }
        }, 50000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("test");
            }
        }, 30000);

    }
}

