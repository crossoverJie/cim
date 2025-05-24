package com.crossoverjie.cim.route.api.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

/**
 * @author zhongcanyu
 * @date 2025/5/11
 * @description
 */
@Builder
public class OfflineMsgReqVO {

    @NotNull(message = "userId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "message received userId", example = "1545574049323")
    private Long receiveUserId ;

    public OfflineMsgReqVO() {
    }

    public OfflineMsgReqVO(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
    }
}
