package com.crossoverjie.cim.common.req;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Function:
 * @author crossoverJie
 * Date: 2017/6/7 下午11:28
 * @since JDK 1.8
 */
public class BaseRequest {


    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "reqNo", example = "1234567890")
    private String reqNo;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "timestamp", example = "0")
    private int timeStamp;



    public BaseRequest() {
        this.setTimeStamp((int)(System.currentTimeMillis() / 1000));
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }


    @Override
    public String toString() {
        return "BaseRequest{" +
                "reqNo='" + reqNo + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
