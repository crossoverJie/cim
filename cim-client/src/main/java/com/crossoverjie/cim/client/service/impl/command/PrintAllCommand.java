package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.common.enums.SystemCommandEnum;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:37
 * @since JDK 1.8
 */
@Service
public class PrintAllCommand implements InnerCommand {


    @Resource
    private Event event ;

    @Override
    public void process(String msg) {
        Map<String, String> allStatusCode = SystemCommandEnum.getAllStatusCode();
        event.info("====================================");
        for (Map.Entry<String, String> stringStringEntry : allStatusCode.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            event.info(key + "----->" + value);
        }
        event.info("====================================");
    }
}
