package com.crossoverjie.cim.client.scanner;

import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.service.MsgLogger;
import com.crossoverjie.cim.client.util.SpringBeanFactory;
import java.util.Scanner;
import lombok.SneakyThrows;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/21 16:44
 * @since JDK 1.8
 */
public class Scan implements Runnable {


    private final MsgHandle msgHandle ;

    private final MsgLogger msgLogger ;
    private final Event event ;

    public Scan() {
        this.msgHandle = SpringBeanFactory.getBean(MsgHandle.class) ;
        this.msgLogger = SpringBeanFactory.getBean(MsgLogger.class) ;
        this.event = SpringBeanFactory.getBean(Event.class) ;
    }

    @SneakyThrows
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String msg = sc.nextLine();

            if (msgHandle.checkMsg(msg)) {
                continue;
            }

            // internal cmd
            if (msgHandle.innerCommand(msg)){
                continue;
            }

            msgHandle.sendMsg(msg) ;

            // write to log
            msgLogger.log(msg) ;

            event.info(msg);
        }
    }

}
