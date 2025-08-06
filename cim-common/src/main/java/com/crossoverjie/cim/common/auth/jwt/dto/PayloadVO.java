package com.crossoverjie.cim.common.auth.jwt.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author chenqwwq
 * @date 2025/6/7
 **/
public class PayloadVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 授权目标主机地址
     */
    private String host;

    /**
     * 授权目标主机端口
     */
    private Integer port;

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

    public String getHost() {
        return host;
    }

    public PayloadVO setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public PayloadVO setPort(Integer port) {
        this.port = port;
        return this;
    }
}
