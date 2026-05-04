package com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper;

import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OfflineMsgShardingMapper {

    int insert(@Param("tableName") String tableName, @Param("msg") OfflineMsg msg);

    int insertBatch(@Param("tableName") String tableName, @Param("offlineMsgs") List<OfflineMsg> offlineMsgs);

    List<OfflineMsg> fetchOfflineMsgsWithCursor(@Param("tableName") String tableName,
                                                   @Param("receiveUserId") Long receiveUserId,
                                                   @Param("lastMessageId") Long lastMessageId,
                                                   @Param("limit") Integer limit);

    int updateStatus(@Param("tableName") String tableName,
                     @Param("receiveUserId") Long receiveUserId,
                     @Param("messageIds") List<Long> messageIds);

    List<Long> fetchOfflineMsgIdsWithCursor(@Param("tableName") String tableName,
                                              @Param("receiveUserId") Long receiveUserId,
                                              @Param("lastMessageId") Long lastMessageId);

    int deleteExpiredMessages(@Param("tableName") String tableName,
                               @Param("expireTime") LocalDateTime expireTime);

    int countPendingMessages(@Param("tableName") String tableName,
                              @Param("receiveUserId") Long receiveUserId);

    OfflineMsg selectByMessageId(@Param("tableName") String tableName,
                                  @Param("messageId") Long messageId);
}
