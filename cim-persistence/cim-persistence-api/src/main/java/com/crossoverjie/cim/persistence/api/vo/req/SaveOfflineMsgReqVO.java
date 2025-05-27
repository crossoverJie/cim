package com.crossoverjie.cim.persistence.api.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class SaveOfflineMsgReqVO extends BaseRequest {

    @NotNull(message = "msg 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "msg", example = "hello")
    private String msg ;

    @NotNull(message = "userId 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "userId", example = "11")
    private Long receiveUserId;

    @Setter
    @Getter
    private Map<String, String> properties;

}
