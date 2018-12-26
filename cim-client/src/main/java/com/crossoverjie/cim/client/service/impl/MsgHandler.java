package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.GroupReqVO;
import com.crossoverjie.cim.client.vo.req.P2PReqVO;
import com.crossoverjie.cim.common.enums.SystemCommandEnumType;
import com.crossoverjie.cim.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/26 11:15
 * @since JDK 1.8
 */
@Service
public class MsgHandler implements MsgHandle {
    private final static Logger LOGGER = LoggerFactory.getLogger(MsgHandler.class);
    @Autowired
    private RouteRequest routeRequest ;

    @Override
    public void groupChat(GroupReqVO groupReqVO) throws Exception {
        routeRequest.sendGroupMsg(groupReqVO);
    }

    @Override
    public void p2pChat(P2PReqVO p2PReqVO) throws Exception {

    }

    @Override
    public boolean checkMsg(String msg) {
        if (StringUtil.isEmpty(msg)){
            LOGGER.warn("不能发送空消息！");
            return true;
        }
        return false;
    }

    @Override
    public boolean innerCommand(String msg) {

        if (msg.startsWith(":")){
            Map<String, String> allStatusCode = SystemCommandEnumType.getAllStatusCode();

            if (SystemCommandEnumType.QUIT.getCommandType().trim().equals(msg)){
                LOGGER.info("系统关闭中。。。。");
                System.exit(0);
            } else if (SystemCommandEnumType.ALL.getCommandType().trim().equals(msg)){
                printAllCommand(allStatusCode);
            }else {
                printAllCommand(allStatusCode);
            }

            return true ;

        }else {
            return false ;
        }


    }

    private void printAllCommand(Map<String, String> allStatusCode) {
        LOGGER.warn("====================================");
        for (Map.Entry<String, String> stringStringEntry : allStatusCode.entrySet()) {
            String key = stringStringEntry.getKey();
            String value = stringStringEntry.getValue();
            LOGGER.warn(key + "----->" + value);
        }
        LOGGER.warn("====================================");
    }
}
