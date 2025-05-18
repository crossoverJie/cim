package com.crossoverjie.cim.route.api;

import com.crossoverjie.cim.common.core.proxy.Request;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.route.api.vo.req.*;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;

import java.util.Set;

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
    BaseResponse<NULLBody> groupRoute(ChatReqVO groupReqVO);

    /**
     * Point to point chat
     * @param p2pRequest
     * @return
     * @throws Exception
     */
    BaseResponse<NULLBody> p2pRoute(P2PReqVO p2pRequest);


    /**
     * Offline account
     *
     * @param groupReqVO
     * @return
     * @throws Exception
     */
    BaseResponse<NULLBody> offLine(ChatReqVO groupReqVO);

    /**
     * Login account
     * @param loginReqVO
     * @return
     * @throws Exception
     */
    BaseResponse<CIMServerResVO> login(LoginReqVO loginReqVO) throws Exception;

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
    @Request(method = Request.GET)
    BaseResponse<Set<CIMUserInfo>> onlineUser() throws Exception;


    BaseResponse<NULLBody> fetchOfflineMsgs(OfflineMsgReqVO offlineMsgReqVO);
    // TODO: 2024/8/19  Get cache server & metastore server
}
