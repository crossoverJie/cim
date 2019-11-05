package com.crossoverjie.cim.common.req;

/**
 * Function:
 * @author georgeyang
 * Date: 2019/11/05 下午17:28
 * @since JDK 1.8
 */
public class WebSocketRequest {
    private Long requestId;
    private String reqMsg;
    private Integer type;

    public WebSocketRequest(Long requestId, String reqMsg, Integer type) {
        this.requestId = requestId;
        this.reqMsg = reqMsg;
        this.type = type;
    }

    public WebSocketRequest() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "WebSocketRequest{" +
                "requestId=" + requestId +
                ", reqMsg='" + reqMsg + '\'' +
                ", type=" + type +
                '}';
    }
}
