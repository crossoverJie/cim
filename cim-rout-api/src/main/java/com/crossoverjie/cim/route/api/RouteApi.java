package com.crossoverjie.cim.route.api;

import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.req.RegisterInfoReqVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;

/**
 * Function: Route Api
 *
 * @author crossoverJie
 * Date: 2020-04-24 23:43
 * @since JDK 1.8
 */
public interface RouteApi {

    /**
     * group chat
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    Object groupRoute(ChatReqVO groupReqVO) throws Exception;

    /**
     * Point to point chat
     * @param p2pRequest
     * @return
     * @throws Exception
     */
    Object p2pRoute(P2PReqVO p2pRequest) throws Exception;


    /**
     * Offline account
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    Object offLine(ChatReqVO groupReqVO) throws Exception;

    /**
     * Login account
     * @param loginReqVO
     * @return
     * @throws Exception
     */
    Object login(LoginReqVO loginReqVO) throws Exception;

    /**
     * Register account
     *
     * @param registerInfoReqVO
     * @return
     * @throws Exception
     */
    BaseResponse<RegisterInfoResVO> registerAccount(RegisterInfoReqVO registerInfoReqVO) throws Exception;

    /**
     * Get all online users
     *
     * @return
     * @throws Exception
     */
    Object onlineUser() throws Exception;
}
