package com.crossoverjie.cim.client.service;

import com.crossoverjie.cim.client.vo.req.GroupReqVO;
import com.crossoverjie.cim.client.vo.req.P2PReqVO;

/**
 * Function:消息处理器
 *
 * @author crossoverJie
 *         Date: 2018/12/26 11:11
 * @since JDK 1.8
 */
public interface MsgHandle {


    /**
     * 群聊
     * @param groupReqVO 群聊消息 其中的 userId 为发送者的 userID
     */
    void groupChat(GroupReqVO groupReqVO) throws Exception ;

    /**
     * 私聊
     * @param p2PReqVO 私聊请求
     * @throws Exception
     */
    void p2pChat(P2PReqVO p2PReqVO) throws Exception;
}
