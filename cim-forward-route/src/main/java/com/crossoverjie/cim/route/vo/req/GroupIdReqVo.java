package com.crossoverjie.cim.route.vo.req;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Function: 关闭注销群请求
 *
 * @author george.yang
 *         Date: 2019/12/07 15:56
 * @since JDK 1.8
 */
public class GroupIdReqVo {
    @NotNull(message = "群id 不能为空")
    @ApiModelProperty(required = true, value = "chatGroupId", example = "1545574049323")
    private Long chatGroupId ;

    public Long getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(Long chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    @Override
    public String toString() {
        return "DismissGroupReqVo{" +
                "chatGroupId=" + chatGroupId +
                '}';
    }
}
