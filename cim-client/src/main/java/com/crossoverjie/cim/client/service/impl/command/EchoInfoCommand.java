package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.InnerCommand;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:37
 * @since JDK 1.8
 */
@Service
public class EchoInfoCommand implements InnerCommand {

    @Resource
    private Client client;

    @Resource
    private Event event ;

    @Override
    public void process(String msg) {
        event.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        event.info("client info={}", client.getAuth());
        event.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
