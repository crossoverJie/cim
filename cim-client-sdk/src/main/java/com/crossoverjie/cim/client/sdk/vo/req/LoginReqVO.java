package com.crossoverjie.cim.client.sdk.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class LoginReqVO extends BaseRequest{
    private Long userId ;
    private String userName ;

    @Override
    public String toString() {
        return "LoginReqVO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                "} " + super.toString();
    }
}
