package com.crossoverjie.cim.route.api.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhongcanyu
 * @date 2025/5/11
 * @description
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfflineMsgReqVO {

    @NotNull(message = "userId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "message received userId", example = "1545574049323")
    private Long receiveUserId;

}
