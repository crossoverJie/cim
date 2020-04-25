package com.crossoverjie.cim.server.api;

import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-25 14:23
 * @since JDK 1.8
 */
public interface ServerApi {

    /**
     * Push msg to client
     * @param sendMsgReqVO
     * @return
     * @throws Exception
     */
    Object sendMsg(SendMsgReqVO sendMsgReqVO) throws Exception;
}
