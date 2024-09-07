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
public class GroupReqVO extends BaseRequest {

    @NotNull(message = "userId can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "message sender userId", example = "1545574049323")
    private Long userId ;


    @NotNull(message = "msg can't be null")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "msg", example = "hello")
    private String msg ;


    @Override
    public String toString() {
        return "GroupReqVO{" +
                "userId=" + userId +
                ", msg='" + msg + '\'' +
                "} " + super.toString();
    }
}
