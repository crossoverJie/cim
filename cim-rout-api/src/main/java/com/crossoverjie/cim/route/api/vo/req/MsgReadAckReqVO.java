package com.crossoverjie.cim.route.api.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Function: 消息已读回执请求 VO
 *
 * @author crossoverJie
 * Date: 2026/05/02
 * @since JDK 1.8
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgReadAckReqVO extends BaseRequest {

    @NotNull(message = "msgId 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "消息ID", example = "1234567890")
    private Long msgId;

    @NotNull(message = "userId 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "已读用户ID", example = "1545574049323")
    private Long userId;

    @Schema(description = "已读用户名", example = "user1")
    private String userName;

    @Schema(description = "已读时间戳", example = "1622505600000")
    private Long timestamp;
}
