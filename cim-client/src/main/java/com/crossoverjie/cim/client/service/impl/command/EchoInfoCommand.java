package com.crossoverjie.cim.client.service.impl.command;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.impl.ClientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final static Logger LOGGER = LoggerFactory.getLogger(EchoInfoCommand.class);


    @Autowired
    private ClientInfo clientInfo;

    @Override
    public void process(String msg) {
        LOGGER.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        LOGGER.info("client info=[{}]", JSON.toJSONString(clientInfo.get()));
        LOGGER.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
