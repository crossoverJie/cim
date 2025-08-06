package com.crossoverjie.cim.common.enums;

/**
 * @author chenqwwq
 * @date 2025/6/8
 **/
public enum RegistryType {

    NO("no", "不使用注册中心"),

    ZK("zk", "使用zookeeper注册中心");


    private final String code;

    private final String memo;

    RegistryType(String code, String memo) {
        this.memo = memo;
        this.code = code;
    }


    public String getCode() {
        return code;
    }

    public String getMemo() {
        return memo;
    }
}
