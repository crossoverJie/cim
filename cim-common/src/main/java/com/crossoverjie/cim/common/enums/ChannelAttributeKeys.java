package com.crossoverjie.cim.common.enums;

import io.netty.util.AttributeKey;

/**
 * @author chenqwwq
 * @date 2025/6/7
 **/
public interface ChannelAttributeKeys {

    /**
     * 认证的 Token
     * <p>
     * 在客户端保存
     */
    AttributeKey<String> AUTH_TOKEN = AttributeKey.newInstance("auth_token");

    /**
     * 认证结果
     */
    AttributeKey<Boolean> AUTH_RES = AttributeKey.newInstance("auth_res");

    /**
     * 用户id
     */
    AttributeKey<Long> USER_ID = AttributeKey.newInstance("user_id");

    /**
     * 用户名
     */
    AttributeKey<String> USER_NAME = AttributeKey.newInstance("user_name");


}
