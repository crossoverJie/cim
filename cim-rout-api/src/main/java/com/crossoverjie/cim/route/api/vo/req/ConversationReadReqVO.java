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
public class ConversationReadReqVO extends BaseRequest {

    @NotNull(message = "userId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "current user id", example = "1545574049323")
    private Long userId;

    @NotNull(message = "conversationId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "conversation id", example = "p2p_123_456")
    private String conversationId;

    @NotNull(message = "conversationType can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "conversation type: 0-P2P, 1-GROUP", example = "0")
    private Integer conversationType;

    @Schema(description = "before message id, mark all messages before this id as read", example = "1234567890")
    private Long beforeMessageId;

    @Schema(description = "target user id (for P2P conversation)", example = "1545574049323")
    private Long targetUserId;

    @Schema(description = "group id (for GROUP conversation)", example = "10001")
    private Long groupId;
}
