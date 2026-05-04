package com.crossoverjie.cim.route.api.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class P2PMsgReadReqVO extends BaseRequest {

    @NotNull(message = "messageId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "message id", example = "1234567890")
    private Long messageId;

    @NotNull(message = "userId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "current user id", example = "1545574049323")
    private Long userId;

    @NotNull(message = "sendUserId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "send user id", example = "1545574049323")
    private Long sendUserId;

    @Schema(description = "conversation id", example = "p2p_123_456")
    private String conversationId;
}
