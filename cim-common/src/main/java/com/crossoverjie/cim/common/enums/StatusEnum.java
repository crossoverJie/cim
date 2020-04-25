package com.crossoverjie.cim.common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author crossoverJie
 */

public enum StatusEnum {

    /** 成功 */
    SUCCESS("9000", "成功"),
    /** 成功 */
    FALLBACK("8000", "FALL_BACK"),
    /** 参数校验失败**/
    VALIDATION_FAIL("3000", "invalid argument"),
    /** 失败 */
    FAIL("4000", "Failure"),

    /** 重复登录 */
    REPEAT_LOGIN("5000", "Repeat login, log out an account please!"),

    /** 请求限流 */
    REQUEST_LIMIT("6000", "请求限流"),

    /** 账号不在线 */
    OFF_LINE("7000", "你选择的账号不在线，请重新选择！"),

    SERVER_NOT_AVAILABLE("7100", "cim server is not available, please try again later!"),

    RECONNECT_FAIL("7200", "Reconnect fail, continue to retry!"),
    /** 登录信息不匹配 */
    ACCOUNT_NOT_MATCH("9100", "The User information you have used is incorrect!"),



    ;


    /** 枚举值码 */
    private final String code;

    /** 枚举描述 */
    private final String message;

    /**
     * 构建一个 StatusEnum 。
     * @param code 枚举值码。
     * @param message 枚举描述。
     */
    private StatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String getCode() {
        return code;
    }

    /**
     * 得到枚举描述。
     * @return 枚举描述。
     */
    public String getMessage() {
        return message;
    }

    /**
     * 得到枚举值码。
     * @return 枚举值码。
     */
    public String code() {
        return code;
    }

    /**
     * 得到枚举描述。
     * @return 枚举描述。
     */
    public String message() {
        return message;
    }

    /**
     * 通过枚举值码查找枚举值。
     * @param code 查找枚举值的枚举值码。
     * @return 枚举值码对应的枚举值。
     * @throws IllegalArgumentException 如果 code 没有对应的 StatusEnum 。
     */
    public static StatusEnum findStatus(String code) {
        for (StatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("ResultInfo StatusEnum not legal:" + code);
    }

    /**
     * 获取全部枚举值。
     *
     * @return 全部枚举值。
     */
    public static List<StatusEnum> getAllStatus() {
        List<StatusEnum> list = new ArrayList<StatusEnum>();
        for (StatusEnum status : values()) {
            list.add(status);
        }
        return list;
    }

    /**
     * 获取全部枚举值码。
     *
     * @return 全部枚举值码。
     */
    public static List<String> getAllStatusCode() {
        List<String> list = new ArrayList<String>();
        for (StatusEnum status : values()) {
            list.add(status.code());
        }
        return list;
    }
}
