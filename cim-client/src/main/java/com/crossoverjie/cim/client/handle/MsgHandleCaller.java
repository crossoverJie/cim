package com.crossoverjie.cim.client.handle;

import com.crossoverjie.cim.client.service.CustomMsgHandleListener;

/**
 * Function:消息回调 bean
 *
 * @author crossoverJie
 *         Date: 2018/12/26 17:37
 * @since JDK 1.8
 */
public class MsgHandleCaller {

    /**
     * 回调接口
     */
    private CustomMsgHandleListener msgHandleListener ;

    public MsgHandleCaller(CustomMsgHandleListener msgHandleListener) {
        this.msgHandleListener = msgHandleListener;
    }

    public CustomMsgHandleListener getMsgHandleListener() {
        return msgHandleListener;
    }

    public void setMsgHandleListener(CustomMsgHandleListener msgHandleListener) {
        this.msgHandleListener = msgHandleListener;
    }
}
