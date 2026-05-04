package com.crossoverjie.cim.persistence.mysql.readreceipt.mapper;

import com.crossoverjie.cim.persistence.api.pojo.P2PMsgReadStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface P2PMsgReadStatusMapper {

    int insert(@Param("tableName") String tableName, @Param("status") P2PMsgReadStatus status);

    int insertBatch(@Param("tableName") String tableName, @Param("list") List<P2PMsgReadStatus> list);

    P2PMsgReadStatus selectByMessageIdAndUserId(@Param("tableName") String tableName,
                                                  @Param("messageId") Long messageId,
                                                  @Param("receiveUserId") Long receiveUserId);

    List<P2PMsgReadStatus> selectUnreadByConversation(@Param("tableName") String tableName,
                                                        @Param("receiveUserId") Long receiveUserId,
                                                        @Param("conversationId") String conversationId);

    int updateReadStatus(@Param("tableName") String tableName,
                         @Param("messageId") Long messageId,
                         @Param("receiveUserId") Long receiveUserId);

    int updateReadStatusByConversation(@Param("tableName") String tableName,
                                        @Param("receiveUserId") Long receiveUserId,
                                        @Param("conversationId") String conversationId,
                                        @Param("beforeMessageId") Long beforeMessageId);

    long countUnreadByConversation(@Param("tableName") String tableName,
                                    @Param("receiveUserId") Long receiveUserId,
                                    @Param("conversationId") String conversationId);
}
