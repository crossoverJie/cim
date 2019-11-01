package com.crossoverjie.cim.route.vo.req;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class AddGroupMemberReqVo {

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
