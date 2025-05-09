package com.crossoverjie.cim.server.service;

import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.pojo.OfflineMsg;

import java.util.List;

public interface OfflineMsgService {

    /**
     * Save offline messages
     * @param offlineMsg
     */
    void save(OfflineMsg offlineMsg);


    /**
     * After the client goes online, it retrieves messages in pages based on the cursor (the id of the last pushed offline message stored)
     * @param userId
     * @param lastMessageId
     * @param limit
     * @return
     */
    List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, String lastMessageId, int limit);

}
