package com.crossoverjie.cim.route.vo.req;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Function: 移除群成员请求
 *
 * @author george.yang
 *         Date: 2019/11/21 15:56
 * @since JDK 1.8
 */
public class RemoveGroupMembersReqVo {
    @NotNull(message = "群id 不能为空")
    @ApiModelProperty(required = true, value = "chatGroupId", example = "1545574049323")
    private Long chatGroupId ;

    @NotNull(message = "移除人的 userId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "1545574049323,154557405,12358753432")
    private String userIds ;

    public Long getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(Long chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    @Override
    public String toString() {
        return "AddGroupMemberReqVo{" +
                "chatGroupId='" + chatGroupId + '\'' +
                ", userIds=" + userIds +
                '}';
    }

}
