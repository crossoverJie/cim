package com.crossoverjie.cim.route.service;

import java.util.Map;
import java.util.Set;

/**
 * Function: 消息已读状态服务
 *
 * @author crossoverJie
 * Date: 2026/05/02
 * @since JDK 1.8
 */
public interface MsgReadService {

    /**
     * 记录用户已读某条消息
     * @param msgId 消息ID
     * @param userId 用户ID
     * @param userName 用户名
     */
    void markAsRead(long msgId, long userId, String userName);

    /**
     * 查询用户是否已读某条消息
     * @param msgId 消息ID
     * @param userId 用户ID
     * @return 是否已读
     */
    boolean isRead(long msgId, long userId);

    /**
     * 获取消息的已读用户列表
     * @param msgId 消息ID
     * @return 已读用户ID集合（userId -> userName）
     */
    Map<Long, String> getReadUsers(long msgId);

    /**
     * 获取消息的已读用户数量
     * @param msgId 消息ID
     * @return 已读用户数量
     */
    long getReadCount(long msgId);

    /**
     * 删除消息的已读状态
     * @param msgId 消息ID
     */
    void removeMsgReadStatus(long msgId);

    /**
     * 同步已读状态到其他服务器（集群环境）
     * @param msgId 消息ID
     * @param userId 用户ID
     * @param userName 用户名
     * @param timestamp 时间戳
     */
    void syncReadStatusToCluster(long msgId, long userId, String userName, long timestamp);
}
