package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.common.data.construct.RingBufferWheel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-09-25 00:37
 * @since JDK 1.8
 */
@Service
@Slf4j
public class DelayMsgCommand implements InnerCommand {

    @Resource
    private Event event;

    @Resource
    private MsgHandle msgHandle ;

    @Resource
    private RingBufferWheel ringBufferWheel ;

    @Override
    public void process(String msg) {
        if (msg.split(" ").length <=2){
            event.info("incorrect commond, :delay [msg] [delayTime]") ;
            return ;
        }

        String message = msg.split(" ")[1] ;
        int delayTime = Integer.parseInt(msg.split(" ")[2]);

        RingBufferWheel.Task task = new DelayMsgJob(message) ;
        task.setKey(delayTime);
        ringBufferWheel.addTask(task);
        event.info(msg);
    }



    private class DelayMsgJob extends RingBufferWheel.Task{

        private String msg ;

        public DelayMsgJob(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                msgHandle.sendMsg(msg);
            } catch (Exception e) {
                log.error("Delay message send error",e);
            }
        }
    }
}
