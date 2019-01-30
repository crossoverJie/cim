package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.common.enums.SystemCommandEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:37
 * @since JDK 1.8
 */
@Service
public class PrintAllCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(PrintAllCommand.class);

    @Override
    public void process(String msg) {
        Map<String, String> allStatusCode = SystemCommandEnum.getAllStatusCode();
        LOGGER.warn("====================================");
        for (Map.Entry<String, String> stringStringEntry : allStatusCode.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            LOGGER.warn(key + "----->" + value);
        }
        LOGGER.warn("====================================");
    }
}
