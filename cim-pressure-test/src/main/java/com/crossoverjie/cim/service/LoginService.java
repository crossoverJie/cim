package com.crossoverjie.cim.service;

import com.crossoverjie.cim.vo.req.LoginReqVO;
import com.crossoverjie.cim.vo.res.CIMServerResVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-06-15 00:43
 * @since JDK 1.8
 */
@Component
public class LoginService {

    private final static Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private RouteRequest routeRequest;

    public CIMServerResVO.ServerInfo userLogin() {
        LoginReqVO loginReqVO = new LoginReqVO(1L, "abc" );
        CIMServerResVO.ServerInfo cimServer = null;
        try {
            cimServer = routeRequest.getCIMServer(loginReqVO);

        } catch (Exception e) {
            LOGGER.error("Login error", e);
            System.exit(-1);
        }
        return cimServer;
    }
}
