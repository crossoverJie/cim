package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.MsgHandle;
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
public class OpenAIModelCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(OpenAIModelCommand.class);


    @Autowired
    private MsgHandle msgHandle ;

    @Override
    public void process(String msg) {
        msgHandle.openAIModel();
        System.out.println("\033[31;4m" + "Hello,我是估值两亿的 AI 机器人！" + "\033[0m");
    }
}
