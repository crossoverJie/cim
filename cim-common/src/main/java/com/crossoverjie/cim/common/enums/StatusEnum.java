package com.crossoverjie.cim.common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author crossoverJie
 */

public enum StatusEnum {

    /** Success */
    SUCCESS("9000", "Success"),
    /** Success */
    FALLBACK("8000", "FALL_BACK"),
    /** Parameter validation failed **/
    VALIDATION_FAIL("3000", "invalid argument"),
    /** Failure */
    FAIL("4000", "Failure"),

    /** Repeat login */
    REPEAT_LOGIN("5000", "Repeat login, log out an account please!"),

    /** Request rate limited */
    REQUEST_LIMIT("6000", "Request rate limited"),

    /** Account offline */
    OFF_LINE("7000", "You selected user is offline!, please try again later!"),

    SERVER_NOT_AVAILABLE("7100", "cim server is not available, please try again later!"),

    RECONNECT_FAIL("7200", "Reconnect fail, continue to retry!"),
    /** Login info mismatch */
    ACCOUNT_NOT_MATCH("9100", "The User information you have used is incorrect!"),

    OFFLINE_MESSAGE_STORAGE_ERROR("9200", "Offline message storage error!"),

    OFFLINE_MESSAGE_FETCH_ERROR("9201", "Offline message fetch error!"),

    OFFLINE_MESSAGE_DELETE_ERROR("9202", "Offline message delete error!");


    /** Enum code */
    private final String code;

    /** Enum description */
    private final String message;

    /**
     * Constructs a StatusEnum.
     * @param code Enum code.
     * @param message Enum description.
     */
    private StatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Gets the enum code.
     * @return Enum code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the enum description.
     * @return Enum description.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the enum code.
     * @return Enum code.
     */
    public String code() {
        return code;
    }

    /**
     * Gets the enum description.
     * @return Enum description.
     */
    public String message() {
        return message;
    }

    /**
     * Finds the enum by code.
     * @param code The code to search for.
     * @return The enum value corresponding to the code.
     * @throws IllegalArgumentException if code has no corresponding StatusEnum.
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
     * Gets all enum values.
     *
     * @return All enum values.
     */
    public static List<StatusEnum> getAllStatus() {
        List<StatusEnum> list = new ArrayList<StatusEnum>();
        for (StatusEnum status : values()) {
            list.add(status);
        }
        return list;
    }

    /**
     * Gets all enum codes.
     *
     * @return All enum codes.
     */
    public static List<String> getAllStatusCode() {
        List<String> list = new ArrayList<String>();
        for (StatusEnum status : values()) {
            list.add(status.code());
        }
        return list;
    }
}
