package com.crossoverjie.cim.persistence.api.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
@Data
@Builder
@AllArgsConstructor
public class SaveOfflineMsgReqVO extends BaseRequest {

    @NotNull(message = "msg 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "msg", example = "hello")
    private String msg ;

    @NotNull(message = "userId 不能为空")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "userId", example = "11")
    private Long receive_user_id ;

    @Setter
    @Getter
    private Map<String, String> properties;

    public SaveOfflineMsgReqVO() {
    }

    public SaveOfflineMsgReqVO(String msg, Long receive_user_id) {
        this.msg = msg;
        this.receive_user_id = receive_user_id;
    }
}
