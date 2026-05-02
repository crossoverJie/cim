package com.crossoverjie.cim.route.service;

/**
 * Function: 消息元数据服务
 * 用于存储消息ID与发送者ID的映射关系
 *
 * @author crossoverJie
 * Date: 2026/05/02
 * @since JDK 1.8
 */
public interface MsgMetadataService {

    /**
     * 保存消息发送者信息
     * @param msgId 消息ID
     * @param sendUserId 发送者用户ID
     */
    void saveMsgSender(long msgId, long sendUserId);

    /**
     * 获取消息发送者ID
     * @param msgId 消息ID
     * @return 发送者用户ID，如果不存在则返回 null
     */
    Long getMsgSender(long msgId);

    /**
     * 删除消息元数据
     * @param msgId 消息ID
     */
    void removeMsgMetadata(long msgId);
}
