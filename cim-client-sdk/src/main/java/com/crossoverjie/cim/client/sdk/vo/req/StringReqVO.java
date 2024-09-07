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
public class StringReqVO extends BaseRequest {

    @NotNull(message = "msg cannot be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED , description = "msg", example = "hello")
    private String msg ;

}
