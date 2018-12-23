package com.crossoverjie.cim.client.service;

import com.crossoverjie.cim.client.vo.res.CIMServerResVO;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/22 22:26
 * @since JDK 1.8
 */
public interface RouteRequest {

    /**
     * 群发消息
     * @param msg 消息
     * @throws Exception
     */
    void sendGroupMsg(String msg) throws Exception;

    /**
     * 获取服务器
     * @return 服务ip+port
     * @throws Exception
     */
    CIMServerResVO.ServerInfo getCIMServer() throws Exception;
}
