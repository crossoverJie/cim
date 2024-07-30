package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.service.EchoService;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.res.OnlineUsersResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private RouteRequest routeRequest ;

    @Autowired
    private EchoService echoService ;

    @Override
    public void process(String msg) {
        try {
            List<OnlineUsersResVO.DataBodyBean> onlineUsers = routeRequest.onlineUsers();

            echoService.echo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            for (OnlineUsersResVO.DataBodyBean onlineUser : onlineUsers) {
                echoService.echo("userId={}=====userName={}",onlineUser.getUserId(),onlineUser.getUserName());
            }
            echoService.echo("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        } catch (Exception e) {
            log.error("Exception", e);
        }
    }
}
