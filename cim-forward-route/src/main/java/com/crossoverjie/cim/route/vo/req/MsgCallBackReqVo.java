package com.crossoverjie.cim.route.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;

/**
 * Function: 默认消息回调请求数据
 *
 * @author georgeyang
 *         Date: 2019/11/08 15:56
 * @since JDK 1.8
 */
public class MsgCallBackReqVo extends BaseRequest {
    private Long sendUserId;
    private Long receiveUserId;
    private String msg;

    public MsgCallBackReqVo() {
    }

    public MsgCallBackReqVo(Long sendUserId, Long receiveUserId, String msg) {
        this.sendUserId = sendUserId;
        this.receiveUserId = receiveUserId;
        this.msg = msg;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "MsgCallBackReqVo{" +
                "sendUserId=" + sendUserId +
                ", receiveUserId=" + receiveUserId +
                ", msg='" + msg + '\'' +
                '}';
    }
}
