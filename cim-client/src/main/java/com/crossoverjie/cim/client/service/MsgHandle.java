package com.crossoverjie.cim.client.service;

import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;

/**
 * Function:消息处理器
 *
 * @author crossoverJie
 * Date: 2018/12/26 11:11
 * @since JDK 1.8
 */
public interface MsgHandle {

    /**
     * 统一的发送接口，包含了 groupChat p2pChat
     *
     * @param msg
     */
    void sendMsg(String msg) throws Exception;


    /**
     * 校验消息
     *
     * @param msg
     * @return 不能为空，后续可以加上一些敏感词
     * @throws Exception
     */
    boolean checkMsg(String msg);

    /**
     * 执行内部命令
     *
     * @param msg
     * @return 是否应当跳过当前消息（包含了":" 就需要跳过）
     */
    boolean innerCommand(String msg) throws Exception;


    /**
     * 开启 AI 模式
     */
    void openAIModel();

    /**
     * 关闭 AI 模式
     */
    void closeAIModel();
}
