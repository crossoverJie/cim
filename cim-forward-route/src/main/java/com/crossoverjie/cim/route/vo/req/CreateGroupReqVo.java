package com.crossoverjie.cim.route.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Function: 创建群聊请求
 *
 * @author georgeyang
 *         Date: 2019/11/01 15:56
 * @since JDK 1.8
 */
public class CreateGroupReqVo extends BaseRequest {
    @NotNull(message = "创建人 userId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "1545574049323")
    private Long userId ;

    @NotNull(message = "群账号 不能为空")
    @ApiModelProperty(required = true, value = "chatGroupName", example = "1545574049323")
    private String chatGroupName ;

    @NotNull(message = "成员id 不能为空")
    @ApiModelProperty(required = true, value = "members", example = "123,234,145,1545574049323")
    private String members;

    public CreateGroupReqVo() {
    }

    public CreateGroupReqVo(Long userId, String chatGroupName, String members) {
        this.userId = userId;
        this.chatGroupName = chatGroupName;
        this.members = members;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getChatGroupName() {
        return chatGroupName;
    }

    public void setChatGroupName(String chatGroupName) {
        this.chatGroupName = chatGroupName;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "CreateGroupReqVo{" +
                "userId=" + userId +
                ", chatGroupName='" + chatGroupName + '\'' +
                ", members='" + members + '\'' +
                '}';
    }
}
