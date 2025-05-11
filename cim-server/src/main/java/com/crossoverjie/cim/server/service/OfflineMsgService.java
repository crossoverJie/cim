package com.crossoverjie.cim.server.service;

import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.pojo.OfflineMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OfflineMsgService {

    /**
     * Save offline messages
     *
     * @param offlineMsg
     */
    void save(OfflineMsg offlineMsg);


    /**
     * After the client goes online, it retrieves messages in pages based on the cursor (the id of the last pushed offline message stored)
     *
     * @param userId
     * @param limit
     * @return
     */
    List<OfflineMsg> fetchOfflineMsgsWithCursor(Long userId, int limit);

    OfflineMsg createFromVo(SendMsgReqVO vo);

    void updateStatus(Long userId, List<String> messageIds);

    List<String> fetchOfflineMsgIdsWithCursor(Long userId);

    int insertBatch(List<OfflineMsg> offlineMsgs);
}
