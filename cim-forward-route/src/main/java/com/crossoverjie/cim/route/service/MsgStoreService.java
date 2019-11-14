package com.crossoverjie.cim.route.service;

import com.crossoverjie.cim.route.vo.store.StoreMsgVo;

import java.util.List;

/**
 * Function: 消息服务，存储在redis缓存
 *
 * @author georgeYange
 *         Date: 2019/10/30 14:02
 * @since JDK 1.8
 */
public interface MsgStoreService {
    /**
     * 获取缓存的消息内容
     * @param uuid
     * @return
     */
    StoreMsgVo getMessage(String uuid);

    /**
     * uuid的消息是否存在
     * @param uuid
     * @return
     */
    boolean isMessageExist(String uuid);

    /**
     * put存储一条消息
     * @param uuid
     * @param senderUserId
     * @param receiverUserId
     * @param msg
     */
    boolean putMessage(String uuid, Long senderUserId, Long receiverUserId, String msg);

    /**
     * put存储一条消息
     */
    boolean putMessage(StoreMsgVo storeMsgVo);

    /**
     * 给用户添加离线消息记录
     * @param userId
     * @param uuid
     */
    boolean addUserOffLineMessage(Long userId, String uuid);

    /**
     * 获取用户离线消息
     * @param userId
     * @param limit
     * @return
     */
    List<String> getUserOffLineMessageUUId(Long userId, int limit);

    /**
     * 设置某条离线消息已读，true成功，false失败或消息id不对全部移除
     * @param userId
     * @return
     */
    boolean setUserOffLineMessageRead(Long userId, String uuid);
}
