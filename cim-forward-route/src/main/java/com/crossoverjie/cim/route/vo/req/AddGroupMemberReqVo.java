package com.crossoverjie.cim.route.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Function: 加入群成员请求
 *
 * @author george.yang
 *         Date: 2019/11/21 15:56
 * @since JDK 1.8
 */
public class AddGroupMemberReqVo extends BaseRequest {

    @NotNull(message = "群id 不能为空")
    @ApiModelProperty(required = true, value = "chatGroupId", example = "1545574049323")
    private Long chatGroupId ;

    @NotNull(message = "加入人 userId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "1545574049323")
    private Long userId ;

    public Long getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(Long chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AddGroupMemberReqVo{" +
                "chatGroupId='" + chatGroupId + '\'' +
                ", userId=" + userId +
                '}';
    }
}
