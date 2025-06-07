package com.crossoverjie.cim.common.auth.jwt.dto;

import java.io.Serializable;

/**
 * @author chenqwwq
 * @date 2025/6/7
 **/
public class PayloadVO implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    public Long getUserId() {
        return userId;
    }

    public PayloadVO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public PayloadVO setUserName(String userName) {
        this.userName = userName;
        return this;
    }
}
