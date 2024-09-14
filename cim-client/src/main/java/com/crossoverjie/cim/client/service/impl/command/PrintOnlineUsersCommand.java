package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import jakarta.annotation.Resource;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
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
public class PrintOnlineUsersCommand implements InnerCommand {

    @Resource
    private Client client ;

    @Resource
    private Event event ;

    @Override
    public void process(String msg) {
        try {
            Set<CIMUserInfo> onlineUsers = client.getOnlineUser();

            event.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            for (CIMUserInfo onlineUser : onlineUsers) {
                event.info("userId={}=====userName={}",onlineUser.getUserId(),onlineUser.getUserName());
            }
            event.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        } catch (Exception e) {
            log.error("Exception", e);
        }
    }
}
