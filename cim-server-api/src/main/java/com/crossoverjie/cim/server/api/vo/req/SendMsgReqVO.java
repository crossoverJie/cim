package com.crossoverjie.cim.server.api.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
public class SendMsgReqVO extends BaseRequest {

    @NotNull(message = "msg 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "msg", example = "hello")
    private String msg ;

    @NotNull(message = "userId 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "userId", example = "11")
    private Long userId ;

    @Setter
    @Getter
    private Map<String, String> properties;

    public SendMsgReqVO() {
    }

    public SendMsgReqVO(String msg, Long userId) {
        this.msg = msg;
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SendMsgReqVO{" +
                "msg='" + msg + '\'' +
                ", userId=" + userId +
                "} " + super.toString();
    }
}
