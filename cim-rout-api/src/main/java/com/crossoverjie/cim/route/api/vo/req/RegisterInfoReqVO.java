package com.crossoverjie.cim.route.api.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 22:04
 * @since JDK 1.8
 */
public class RegisterInfoReqVO extends BaseRequest {

    @NotNull(message = "用户名不能为空")
    @ApiModelProperty(required = true, value = "userName", example = "zhangsan")
    private String userName ;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "RegisterInfoReqVO{" +
                "userName='" + userName + '\'' +
                "} " + super.toString();
    }
}
