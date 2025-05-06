package com.crossoverjie.cim.msg.storage.service;

import com.crossoverjie.cim.msg.storage.pojo.OfflineMsg;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;

import java.util.List;

public interface OfflineMsgService {

    /**
     * Save offline messages
     *
     * @param p2PReqVO
     */
    void save(P2PReqVO p2PReqVO);


    /**
     * After the client goes online, it retrieves messages in pages based on the cursor (the id of the last pushed offline message stored)
     * @param userId
     * @param lastMessageId
     * @param limit
     * @return
     */
    List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, String lastMessageId, int limit);

}
