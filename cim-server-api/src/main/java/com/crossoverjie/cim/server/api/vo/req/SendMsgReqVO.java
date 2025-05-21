package com.crossoverjie.cim.server.api.vo.req;

import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.req.BaseRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
@Builder
@AllArgsConstructor
public class SendMsgReqVO extends BaseRequest {

    @NotNull(message = "msg 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "msg", example = "hello")
    private String msg ;

    @Getter
    @Setter
    private List<String> batchMsg;

    @NotNull(message = "userId 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "userId", example = "11")
    private Long userId ;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "cmd", example = "message")
    private BaseCommand cmd;

    @Setter
    @Getter
    private Map<String, String> properties;

    public SendMsgReqVO() {
    }

    public BaseCommand getCmd() {
        return cmd;
    }

    public void setCmd(BaseCommand cmd) {
        this.cmd = cmd;
    }

    public SendMsgReqVO(String msg, Long userId, List<String> batchMsg) {
        this.msg = msg;
        this.userId = userId;
        this.batchMsg = batchMsg;
    }

    public SendMsgReqVO(String msg, Long userId, List<String> batchMsg, BaseCommand cmd) {
        this.msg = msg;
        this.batchMsg = batchMsg;
        this.userId = userId;
        this.cmd = cmd;
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
                ", batchMsg=" + batchMsg +
                ", userId=" + userId +
                ", properties=" + properties +
                '}';
    }
}
