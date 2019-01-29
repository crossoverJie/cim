package com.crossoverjie.cim.client.service;

import com.crossoverjie.cim.client.util.SpringBeanFactory;
import com.crossoverjie.cim.common.enums.SystemCommandEnumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:39
 * @since JDK 1.8
 */
@Component
public class InnerCommandContext {
    private final static Logger LOGGER = LoggerFactory.getLogger(InnerCommandContext.class);

    /**
     * 获取执行器
     * @param command 执行器
     * @return
     */
    public InnerCommand execute(String command) {

        Map<String, String> allClazz = SystemCommandEnumType.getAllClazz();
        String clazz = allClazz.get(command);
        InnerCommand innerCommand = null;
        try {
            innerCommand = (InnerCommand) SpringBeanFactory.getBean(Class.forName(clazz));
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }

        return innerCommand;
    }

}
