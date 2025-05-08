package com.crossoverjie.cim.msg.storage.mapper;

import com.crossoverjie.cim.msg.storage.pojo.OfflineMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/9
 * @description
 */
@Mapper
public interface OfflineMsgMapper {

    int insert(OfflineMsg msg);

    List<OfflineMsg> fetchOfflineMsgsWithCursor(@Param("userId") Long userId, @Param("lastMessageId") String lastMessageId, @Param("limit") int limit);

    int updateStatus(
            @Param("conversationId") String conversationId,
            @Param("messageIds") List<String> messageIds);
}
