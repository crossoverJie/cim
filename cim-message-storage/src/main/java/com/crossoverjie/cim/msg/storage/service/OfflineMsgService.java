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

    //todo 提供个批量保存的，方便定时任务重 WAL 补推


    /**
     * After the client goes online, it retrieves messages in pages based on the cursor (the id of the last pushed offline message stored)
     * @param conversationId
     * @param userId
     * @param lastMessageId
     * @param limit
     * @return
     */
    List<OfflineMsg> fetchOfflineWithCursor(String conversationId, String userId, String lastMessageId, int limit);

    /**
     * 将离线消息推送给客户端
     */

    /**
     * After the client receives the offline message and ack, it changes the status of this batch of school departure messages to "acked" and updates the session cursor
     */
    void ackAndUpdateCursor(String conversationId, String userId, List<String> messageIds);
}
