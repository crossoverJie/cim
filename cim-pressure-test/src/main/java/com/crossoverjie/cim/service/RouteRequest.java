package com.crossoverjie.cim.service;


import com.crossoverjie.cim.vo.req.LoginReqVO;
import com.crossoverjie.cim.vo.res.CIMServerResVO;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/22 22:26
 * @since JDK 1.8
 */
public interface RouteRequest {

    /**
     * 获取服务器
     * @return 服务ip+port
     * @param loginReqVO
     * @throws Exception
     */
    CIMServerResVO.ServerInfo getCIMServer(LoginReqVO loginReqVO) throws Exception;



}
