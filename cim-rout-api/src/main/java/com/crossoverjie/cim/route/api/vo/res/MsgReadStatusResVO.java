package com.crossoverjie.cim.route.api.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Function: 消息已读状态响应 VO
 *
 * @author crossoverJie
 * Date: 2026/05/02
 * @since JDK 1.8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgReadStatusResVO {

    @Schema(description = "消息ID", example = "1234567890")
    private Long msgId;

    @Schema(description = "已读用户数量", example = "3")
    private Long readCount;

    @Schema(description = "群聊成员总数", example = "5")
    private Integer totalMemberCount;

    @Schema(description = "已读用户列表 (userId -> userName)")
    private Map<Long, String> readUsers;

    @Schema(description = "未读用户数量", example = "2")
    private Integer unreadCount;
}
