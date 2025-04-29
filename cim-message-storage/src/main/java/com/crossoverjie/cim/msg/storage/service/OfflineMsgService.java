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
     * 客户端上线后，根据游标（存储的最后推送的离线消息的id）分页获取消息
     */
    List<OfflineMsg> fetchOfflineWithCursor(String conversationId, String userId, int limit);

    /**
     * 将离线消息推送给客户端
     */

    /**
     * 客户端接收到离线消息并且 ack 后，将这批离校消息的状态修改为 acked
     */
    void ackAndUpdateCursor(String conversationId, String userId, List<OfflineMsg> msgs);
}
