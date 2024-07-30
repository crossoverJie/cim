package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.service.EchoService;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.MsgLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:37
 * @since JDK 1.8
 */
@Slf4j
@Service
public class QueryHistoryCommand implements InnerCommand {

    @Autowired
    private MsgLogger msgLogger ;

    @Autowired
    private EchoService echoService ;

    @Override
    public void process(String msg) {
        String[] split = msg.split(" ");
        if (split.length < 2){
            return;
        }
        String res = msgLogger.query(split[1]);
        echoService.echo(res);
    }
}
