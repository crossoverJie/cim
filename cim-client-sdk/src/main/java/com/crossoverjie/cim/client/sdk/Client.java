package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.logger.Printer;
import com.crossoverjie.cim.client.sdk.vo.req.LoginReqVO;
import com.crossoverjie.cim.client.sdk.vo.res.ServerResVO;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
public class Client {

    private long userId;
    private String userName;

    private String routeUrl;

    private int loginFailNumConf;

    private int errorCount;

    private Printer logger;


    private ServerResVO.ServerInfo userLogin() {
        LoginReqVO loginReqVO = new LoginReqVO(userId, userName);
        ServerResVO.ServerInfo cimServer = null;
        try {
//            cimServer = routeRequest.getCIMServer(loginReqVO);
//
//            //保存系统信息
//            clientInfo.saveServiceInfo(cimServer.getIp() + ":" + cimServer.getCimServerPort())
//                    .saveUserInfo(userId, userName);

//            log.info("cimServer=[{}]", cimServer.toString());
        } catch (Exception e) {
            errorCount++;

            if (errorCount >= loginFailNumConf) {
                logger.info("The maximum number of reconnections has been reached[{}]times, close cim client!", errorCount);
                // todo shutdown
            }
            log.error("login fail", e);
        }
        return cimServer;
    }
}
