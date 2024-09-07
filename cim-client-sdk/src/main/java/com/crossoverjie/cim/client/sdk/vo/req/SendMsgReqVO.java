package com.crossoverjie.cim.client.sdk.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class SendMsgReqVO extends BaseRequest {

    @NotNull(message = "msg can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED , description = "msg", example = "hello")
    private String msg ;

    @NotNull(message = "userId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED , description = "userId", example = "11")
    private Long userId ;

}
