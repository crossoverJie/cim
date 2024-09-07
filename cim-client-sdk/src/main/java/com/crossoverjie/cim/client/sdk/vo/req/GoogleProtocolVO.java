package com.crossoverjie.cim.client.sdk.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoogleProtocolVO extends BaseRequest {
    @NotNull(message = "requestId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED , description = "requestId", example = "123")
    private Integer requestId ;

    @NotNull(message = "msg can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "msg", example = "hello")
    private String msg ;
}
