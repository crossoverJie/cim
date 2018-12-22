package com.crossoverjie.cim.common.util;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 15:16
 * @since JDK 1.8
 */
public class StringUtil {
    public StringUtil() {
    }

    public static boolean isNullOrEmpty(String str) {
        return null == str || 0 == str.trim().length();
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

    public static String formatLike(String str) {
        return isNotEmpty(str)?"%" + str + "%":null;
    }
}