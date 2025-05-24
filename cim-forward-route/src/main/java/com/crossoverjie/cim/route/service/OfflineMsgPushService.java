package com.crossoverjie.cim.route.service;

import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;

/**
 * Offline message push service
 */
public interface OfflineMsgPushService {

    /**
     * fetch offline messages
     * @param cimServerResVO
     * @param receiveUserId
     */
    void fetchOfflineMsgs(CIMServerResVO cimServerResVO, Long receiveUserId);

    /**
     * save offline message
     * @param p2pRequest
     */
    void saveOfflineMsg(P2PReqVO p2pRequest);
}
