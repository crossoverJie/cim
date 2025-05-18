package com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper;



import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
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

    List<OfflineMsg> fetchOfflineMsgsWithCursor(@Param("userId") Long userId,  @Param("limit") Integer limit);

    int updateStatus(
            @Param("userId") Long userId,
            @Param("messageIds") List<Long> messageIds);

    List<Long> fetchOfflineMsgIdsWithCursor(@Param("userId") Long userId);

}
