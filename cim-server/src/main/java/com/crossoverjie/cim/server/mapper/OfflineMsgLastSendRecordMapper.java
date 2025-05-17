package com.crossoverjie.cim.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
@Mapper
public interface OfflineMsgLastSendRecordMapper {

    void saveLatestMessageId(@Param("userId") Long userId,@Param("lastMessageId") Long lastMessageId);

    String getLatestMessageId(@Param("userId") Long userId);
}
