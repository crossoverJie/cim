package com.crossoverjie.cim.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Function: 字符串工具类测试
 * Date: 01/04/2026
 */
public class StringUtilTest {


    /**
     * isNullOrEmpty - null值
     */
    @Test
    public void testIsNullOrEmptyWhenNull() {
        String str = null;
        assertTrue(StringUtil.isNullOrEmpty(str), "null 应该返回 true");
    }

    /**
     * isNullOrEmpty - 空字符串
     */
    @Test
    public void testIsNullOrEmptyWhenEmptyString() {
        String str = "";
        assertTrue(StringUtil.isNullOrEmpty(str), "空字符串应该返回 true");
    }

    /**
     * isNullOrEmpty - 纯空格字符串

     */
    @Test
    public void testIsNullOrEmptyWhenBlankSpaces() {
        String str = "   ";
        assertTrue(StringUtil.isNullOrEmpty(str), "纯空格应该返回 true");
    }

    /**
     * isNullOrEmpty - 制表符
     */
    @Test
    public void testIsNullOrEmptyWhenTab() {
        String str = "\t";
        assertTrue(StringUtil.isNullOrEmpty(str), "制表符应该返回 true");
    }

    /**
     * isNullOrEmpty - 换行符
     */
    @Test
    public void testIsNullOrEmptyWhenNewline() {
        String str = "\n";
        assertTrue(StringUtil.isNullOrEmpty(str), "换行符应该返回 true");
    }

    /**
     * isNullOrEmpty - 多个空白字符组合
     */
    @Test
    public void testIsNullOrEmptyWhenMultipleWhitespace() {
        String str = " \t\n ";
        assertTrue(StringUtil.isNullOrEmpty(str), "多个空白字符应该返回 true");
    }

    /**
     * isNullOrEmpty - 正常字符串
     */
    @Test
    public void testIsNullOrEmptyWhenNormalString() {
        String str = "hello";
        assertFalse(StringUtil.isNullOrEmpty(str), "正常字符串应该返回 false");
    }

    /**
     * isNullOrEmpty - 包含空格的字符串
     */
    @Test
    public void testIsNullOrEmptyWhenStringWithSpaces() {
        String str = " hello ";
        assertFalse(StringUtil.isNullOrEmpty(str), "首尾有空格的字符串应该返回 false");
    }


    /**
     * isEmpty - null值

     */
    @Test
    public void testIsEmptyWhenNull() {
        String str = null;
        assertTrue(StringUtil.isEmpty(str), "null 应该返回 true");
    }

    /**
     * isEmpty - 空字符串
     */
    @Test
    public void testIsEmptyWhenEmptyString() {
        String str = "";
        assertTrue(StringUtil.isEmpty(str), "空字符串应该返回 true");
    }

    /**
     * isEmpty - 纯空格字符串
     */
    @Test
    public void testIsEmptyWhenBlankSpaces() {
        String str = "   ";
        assertTrue(StringUtil.isEmpty(str), "纯空格也应该返回 true（因为是空字符串）");
    }

    /**
     * isEmpty - 正常字符串
     */
    @Test
    public void testIsEmptyWhenNormalString() {
        String str = "hello";
        assertFalse(StringUtil.isEmpty(str), "正常字符串应该返回 false");
    }


    /**
     * isNotEmpty - null值
     */
    @Test
    public void testIsNotEmptyWhenNull() {
        String str = null;
        assertFalse(StringUtil.isNotEmpty(str), "null 应该返回 false");
    }

    /**
     * isNotEmpty - 空字符串
     */
    @Test
    public void testIsNotEmptyWhenEmptyString() {
        String str = "";
        assertFalse(StringUtil.isNotEmpty(str), "空字符串应该返回 false");
    }

    /**
     * isNotEmpty - 纯空格字符串
     */
    @Test
    public void testIsNotEmptyWhenBlankSpaces() {
        String str = "   ";
        assertFalse(StringUtil.isNotEmpty(str), "纯空格应该返回 false");
    }

    /**
     * isNotEmpty - 正常字符串
     */
    @Test
    public void testIsNotEmptyWhenNormalString() {
        String str = "hello";
        assertTrue(StringUtil.isNotEmpty(str), "正常字符串应该返回 true");
    }


    /**
     * formatLike - null值
     */
    @Test
    public void testFormatLikeWhenNull() {
        String str = null;
        assertNull(StringUtil.formatLike(str), "null 应该返回 null");
    }

    /**
     * formatLike - 空字符串
     */
    @Test
    public void testFormatLikeWhenEmptyString() {
        String str = "";
        assertNull(StringUtil.formatLike(str), "空字符串应该返回 null");
    }

    /**
     * SQL 注入字符（单引号）的处理
     */
    @Test
    public void testFormatLikeWhenSqlInjectionShouldHandleSafely() {
        String input = "admin' OR '1'='1";
        String result = StringUtil.formatLike(input);

        assertEquals("%admin' OR '1'='1%", result, "结果应该包含原始单引号，且首尾有 %");
        assertTrue(result.startsWith("%") && result.endsWith("%"), "结果必须以 % 开头和结尾");
    }

    /**
     * 测试：SQL 通配符（% 和 _）的处理
     */
    @Test
    public void testFormatLikeWhenWildcardShouldEscapeProperly() {
        String input = "100%_complete";
        String result = StringUtil.formatLike(input);

        assertEquals("%100%_complete%", result, "结果应该包含原始的 % 和 _，且首尾有 %");
        assertTrue(result.contains("%") && result.contains("_"), "结果应该保留原始的通配符");
    }


    /**
     * formatLike - 纯空格字符串
     */
    @Test
    public void testFormatLikeWhenBlankSpaces() {
        String str = "   ";
        assertNull(StringUtil.formatLike(str), "纯空格应该返回 null");
    }

    /**
     * formatLike - 正常字符串
     */
    @Test
    public void testFormatLikeWhenNormalString() {
        String result = StringUtil.formatLike("hello");
        assertEquals("%hello%", result, "应该返回 %hello%");
    }

    /**
     * formatLike - 带空格的字符串
     */
    @Test
    public void testFormatLikeWhenStringWithSpaces() {
        String str = " hello ";
        String result = StringUtil.formatLike(str);
        assertEquals("%hello%", result, "应该返回 %hello%");
    }

    /**
     * formatLike - 中文字符串

     */
    @Test
    public void testFormatLikeWhenChineseString() {
        String str = "测试";
        String result = StringUtil.formatLike(str);
        assertEquals("%测试%", result, "应该返回 %测试%");
    }

    /**
     * formatLike - 特殊字符

     */
    @Test
    public void testFormatLikeWhenSpecialCharacters() {
        String str = "test%_";
        String result = StringUtil.formatLike(str);
        assertEquals("%test%_%", result, "应该返回 %test%_%");
    }


    /**
     * 处理大字符串输入
     */
    @Test
    public void testPerformanceWithLargeString() {
        String largeString = "a".repeat(10000);

        boolean result = assertDoesNotThrow(() -> {
            return StringUtil.isNullOrEmpty(largeString);
        }, "处理大字符串时不应抛出异常");

        assertFalse(result, "长度为 10000 的字符串不应被视为 null 或 empty");
    }

    /**
     * 单字符测试
     */
    @Test
    public void testSingleCharacter() {
        assertFalse(StringUtil.isNullOrEmpty("a"), "单字符应该返回 false");
        assertFalse(StringUtil.isEmpty("a"), "单字符应该返回 false");
        assertTrue(StringUtil.isNotEmpty("a"), "单字符应该返回 true");
        assertEquals("%a%", StringUtil.formatLike("a"), "单字符应该返回 %a%");
    }

    /**
     * 构造函数私有化测试

     */
    @Test
    public void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<?> constructor =
            StringUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThrows(Exception.class, () -> {
            constructor.newInstance();
        }, "私有构造函数不能被调用");
    }
}
