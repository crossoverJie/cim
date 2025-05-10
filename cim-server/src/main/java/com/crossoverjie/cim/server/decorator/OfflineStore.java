package com.crossoverjie.cim.server.decorator;

import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.pojo.OfflineMsg;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
public interface OfflineStore {

    void save(OfflineMsg offlineMsg);

    List<OfflineMsg> fetch(Long userId);

    void markDelivered(Long userId, List<String> messageIds);
}
