package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.MsgHandle;
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
public class CloseAIModelCommand implements InnerCommand {


    @Resource
    private MsgHandle msgHandle ;

    @Resource
    private Event event ;

    @Override
    public void process(String msg) {
        msgHandle.closeAIModel();

        event.info("\033[31;4m" + "｡ﾟ(ﾟ´ω`ﾟ)ﾟ｡  AI 下线了！" + "\033[0m");
    }
}
