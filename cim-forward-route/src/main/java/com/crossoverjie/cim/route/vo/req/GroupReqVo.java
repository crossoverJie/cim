package com.crossoverjie.cim.route.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Function: 发送群组聊天请求数据,对标P2PReqVo
 *
 * @author georgeyang
 * Date: 2019/11/14 17:56
 * @since JDK 1.8
 */
public class GroupReqVo extends BaseRequest {
    @NotNull(message = "groupId 不能为空")
    @ApiModelProperty(required = true, value = "groupId", example = "1545574049323")
    private Long groupId ;

    @NotNull(message = "senderId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "1545574049323")
    private Long senderId ;

    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "msg", example = "hello")
    private String msg ;

    public GroupReqVo() {
    }

    public GroupReqVo(Long groupId, Long senderId, String msg) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    @Override
    public String toString() {
        return "GroupReqVo{" +
                "groupId=" + groupId +
                ", senderId=" + senderId +
                ", msg='" + msg + '\'' +
                '}';
    }
}
