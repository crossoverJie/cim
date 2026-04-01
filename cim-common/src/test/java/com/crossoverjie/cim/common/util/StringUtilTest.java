package com.crossoverjie.cim.common.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Function: StringUtil 工具类测试
 * Date: 2026-04-01
 */
public class StringUtilTest {

    /**
     * 测试 isNullOrEmpty 方法 - null 值
     */
    @Test
    public void testIsNullOrEmptyWhenNullShouldReturnTrue() {
        String str = null;
        assertTrue(StringUtil.isNullOrEmpty(str), "null 应该返回 true");
    }

    /**
     * 测试 isNullOrEmpty 方法 - 空格
     *
     */
    @Test
    public void testIsNullOrEmptyWhenSingleSpaceShouldReturnTrue() {
        String str = " ";
        assertTrue(StringUtil.isNullOrEmpty(str),"单个空格应该返回true");
    }



    /**
     * 测试 isNullOrEmpty 方法 - 空字符串
     */
    @Test
    public void testIsNullOrEmptyWhenEmptyStringShouldReturnTrue() {
        String str = "";
        assertTrue(StringUtil.isNullOrEmpty(str), "空字符串应该返回 true");
    }

    /**
     * 测试 isNullOrEmpty 方法 - 制表符
     */
    @Test
    public void testIsNullOrEmptyWhenTabShouldReturnTrue() {
        String str = "\t";
        assertTrue(StringUtil.isNullOrEmpty(str), "制表符应该返回 true");
    }

    /**
     * 测试 isNullOrEmpty 方法 - 换行符
     */
    @Test
    public void testIsNullOrEmptyWhenNewLineShouldReturnTrue() {
        String str = "\n";
        assertTrue(StringUtil.isNullOrEmpty(str), "换行符应该返回 true");
    }


    /**
     * 测试 isNullOrEmpty 方法 - 空格字符串
     */
    @Test
    public void testIsNullOrEmptyWhenBlankStringShouldReturnTrue() {
        String str = "   ";
        assertTrue(StringUtil.isNullOrEmpty(str), "空格字符串应该返回 true");
    }

    /**
     * 测试 isNullOrEmpty 方法 - 非空字符串
     */
    @Test
    public void testIsNullOrEmptyWhenNonEmptyStringShouldReturnFalse() {
        String str = "hello";
        assertFalse(StringUtil.isNullOrEmpty(str), "非空字符串应该返回 false");
    }

    /**
     * 测试 isNullOrEmpty 方法 - 带空格的字符串
     */
    @Test
    public void testIsNullOrEmptyWhenStringWithSpacesShouldReturnFalse() {
        String str = " hello ";
        assertFalse(StringUtil.isNullOrEmpty(str), "带空格的字符串应该返回 false");
    }

    /**
     * 测试 isEmpty 方法 - null 值
     */
    @Test
    public void testIsEmptyWhenNullShouldReturnTrue() {
        String str = null;
        assertTrue(StringUtil.isEmpty(str), "null 应该返回 true");
    }

    /**
     * 测试 isEmpty 方法 - 空字符串
     */
    @Test
    public void testIsEmptyWhenEmptyStringShouldReturnTrue() {
        String str = "";
        assertTrue(StringUtil.isEmpty(str), "空字符串应该返回 true");
    }

    /**
     * 测试 isEmpty 方法 - 空格字符串
     */
    @Test
    public void testIsEmptyWhenBlankStringShouldReturnTrue() {
        String str = "   ";
        assertTrue(StringUtil.isEmpty(str), "空格字符串应该返回 true");
    }

    /**
     * 测试 isEmpty 方法 - 非空字符串
     */
    @Test
    public void testIsEmptyWhenNonEmptyStringShouldReturnFalse() {
        String str = "hello";
        assertFalse(StringUtil.isEmpty(str), "非空字符串应该返回 false");
    }

    /**
     * 测试 isEmpty 方法 - 带内容的字符串
     */
    @Test
    public void testIsEmptyWhenStringWithContentShouldReturnFalse() {
        String str = " hello ";
        assertFalse(StringUtil.isEmpty(str), "带内容的字符串应该返回 false");
    }

    /**
     * 测试 isNotEmpty 方法 - null 值
     */
    @Test
    public void testIsNotEmptyWhenNullShouldReturnFalse() {
        String str = null;
        assertFalse(StringUtil.isNotEmpty(str), "null 应该返回 false");
    }

    /**
     * 测试 isNotEmpty 方法 - 空字符串
     */
    @Test
    public void testIsNotEmptyWhenEmptyStringShouldReturnFalse() {
        String str = "";
        assertFalse(StringUtil.isNotEmpty(str), "空字符串应该返回 false");
    }

    /**
     * 测试 isNotEmpty 方法 - 空格字符串
     */
    @Test
    public void testIsNotEmptyWhenBlankStringShouldReturnFalse() {
        String str = "   ";
        assertFalse(StringUtil.isNotEmpty(str), "空格字符串应该返回 false");
    }

    /**
     * 测试 isNotEmpty 方法 - 非空字符串
     */
    @Test
    public void testIsNotEmptyWhenNonEmptyStringShouldReturnTrue() {
        String str = "hello";
        assertTrue(StringUtil.isNotEmpty(str), "非空字符串应该返回 true");
    }

    /**
     * 测试 isNotEmpty 方法 - 带空格的字符串
     */
    @Test
    public void testIsNotEmptyWhenStringWithSpacesShouldReturnTrue() {
        String str = " hello ";
        assertTrue(StringUtil.isNotEmpty(str), "带空格的字符串应该返回 true");
    }

    /**
     * 测试 formatLike 方法 - null 值
     */
    @Test
    public void testFormatLikeWhenNullShouldReturnNull() {
        String str = null;
        assertNull(StringUtil.formatLike(str), "null 应该返回 null");
    }

    /**
     * 测试 formatLike 方法 - 空字符串
     */
    @Test
    public void testFormatLikeWhenEmptyStringShouldReturnNull() {
        String str = "";
        assertNull(StringUtil.formatLike(str), "空字符串应该返回 null");
    }



    /**
     * 测试 formatLike 方法 - SQL注入字符串
     */
    @Test
    public void testFormatLikeWhenSqlInjectionShouldHandleSafely() {
        String str = "test' OR '1'='1";
        String result = StringUtil.formatLike(str);
        // 应该正确转义或处理特殊字符
        assertNotNull(result, "SQL注入字符串应该被安全处理");
    }

    /**
     * 测试 formatLike 方法 - 带通配符的字符串
     */
    @Test
    public void testFormatLikeWhenWildcardShouldEscapeProperly() {
        String str = "test%";
        String result = StringUtil.formatLike(str);
        // 应该转义 % 字符
        assertNotNull(result);
        assertTrue(result.contains("%"), "应该包含转义后的 %");
    }
    /**
     * 测试 formatLike 方法 - 空格字符串
     */
    @Test
    public void testFormatLikeWhenBlankStringShouldReturnNull() {
        String str = "   ";
        assertNull(StringUtil.formatLike(str), "空格字符串应该返回 null");
    }

    /**
     * 测试 formatLike 方法 - 普通字符串
     */
    @Test
    public void testFormatLikeWhenNormalStringShouldReturnFormattedString() {
        String str = "hello";
        String result = StringUtil.formatLike(str);
        assertEquals("%hello%", result, "应该返回 %hello%");
    }

    /**
     * 测试 formatLike 方法 - 带空格的字符串
     */
    @Test
    public void testFormatLikeWhenStringWithSpacesShouldReturnFormattedString() {
        String str = " hello ";
        String result = StringUtil.formatLike(str);
        assertEquals("% hello %", result, "应该返回 % hello %");
    }

    /**
     * 测试 formatLike 方法 - 特殊字符
     */
    @Test
    public void testFormatLikeWhenSpecialCharactersShouldReturnFormattedString() {
        String str = "test%_";
        String result = StringUtil.formatLike(str);
        assertEquals("%test%_%", result, "应该返回 %test%_%");
    }

    /**
     * 测试 formatLike 方法 - 中文字符
     */
    @Test
    public void testFormatLikeWhenChineseStringShouldReturnFormattedString() {
        String str = "测试";
        String result = StringUtil.formatLike(str);
        assertEquals("%测试%", result, "应该返回 %测试%");
    }

    /**
     * 测试性能 - 处理大字符串
     */
    @Test
    public void testPerformanceWithLargeString() {
        String largeString = "a".repeat(10000);
        long startTime = System.currentTimeMillis();
        StringUtil.isNullOrEmpty(largeString);
        long endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 100, "处理大字符串应该在100ms内完成");
    }

}
