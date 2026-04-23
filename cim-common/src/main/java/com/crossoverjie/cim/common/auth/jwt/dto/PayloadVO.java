package com.crossoverjie.cim.common.auth.jwt.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author chenqwwq
 * @date 2025/6/7
 **/
@Data
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
}
