package com.crossoverjie.cim.route.vo.store;

/**
 * 可存储的消息
 *
 * @author georgeyang
 *         Date: 2019/11/01 12:30
 * @since JDK 1.8
 */
public class StoreMsgVo {
    private String uuid;
    private Long senderUserId;
    private Long receiverUserId;
    private String msg;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "StoreMsgVo{" +
                "uuid='" + uuid + '\'' +
                ", senderUserId='" + senderUserId + '\'' +
                ", receiverUserId='" + receiverUserId + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
