package com.crossoverjie.cim.server.mapper;



import com.crossoverjie.cim.server.pojo.OfflineMsg;
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

    int insertBatch(@Param("offlineMsgs") List<OfflineMsg> offlineMsgs);

    List<OfflineMsg> fetchOfflineMsgsWithCursor(@Param("userId") Long userId,  @Param("limit") int limit);

    int updateStatus(
            @Param("userId") Long userId,
            @Param("messageIds") List<String> messageIds);

    List<String> fetchOfflineMsgIdsWithCursor(@Param("userId") Long userId);

}
