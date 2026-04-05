package com.crossoverjie.cim.common.enums;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Function: 测试 StatusEnum 枚举类
 * Date: 01/04/2026 20:00
 */
public class StatusEnumTest {

    /**
     * 测试枚举值是否存在
     */
    @Test
    public void testEnumValues() {
        // 测试枚举值存在
        assertNotNull(StatusEnum.SUCCESS);
        assertNotNull(StatusEnum.FAIL);
        assertNotNull(StatusEnum.FALLBACK);
        assertNotNull(StatusEnum.VALIDATION_FAIL);
        assertNotNull(StatusEnum.REPEAT_LOGIN);
        assertNotNull(StatusEnum.REQUEST_LIMIT);
        assertNotNull(StatusEnum.OFF_LINE);
        assertNotNull(StatusEnum.SERVER_NOT_AVAILABLE);
        assertNotNull(StatusEnum.RECONNECT_FAIL);
        assertNotNull(StatusEnum.ACCOUNT_NOT_MATCH);
        assertNotNull(StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR);
        assertNotNull(StatusEnum.OFFLINE_MESSAGE_FETCH_ERROR);
        assertNotNull(StatusEnum.OFFLINE_MESSAGE_DELETE_ERROR);
    }

    /**
     * 测试枚举值码
     */
    @Test
    public void testGetCode() {
        assertEquals("9000", StatusEnum.SUCCESS.getCode());
        assertEquals("8000", StatusEnum.FALLBACK.getCode());
        assertEquals("3000", StatusEnum.VALIDATION_FAIL.getCode());
        assertEquals("4000", StatusEnum.FAIL.getCode());
        assertEquals("5000", StatusEnum.REPEAT_LOGIN.getCode());
        assertEquals("6000", StatusEnum.REQUEST_LIMIT.getCode());
        assertEquals("7000", StatusEnum.OFF_LINE.getCode());
        assertEquals("7100", StatusEnum.SERVER_NOT_AVAILABLE.getCode());
        assertEquals("7200", StatusEnum.RECONNECT_FAIL.getCode());
        assertEquals("9100", StatusEnum.ACCOUNT_NOT_MATCH.getCode());
        assertEquals("9200", StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR.getCode());
        assertEquals("9201", StatusEnum.OFFLINE_MESSAGE_FETCH_ERROR.getCode());
        assertEquals("9202", StatusEnum.OFFLINE_MESSAGE_DELETE_ERROR.getCode());
    }

    /**
     * 测试枚举描述
     */
    @Test
    public void testGetMessage() {
        assertEquals("Success", StatusEnum.SUCCESS.getMessage());
        assertEquals("FALL_BACK", StatusEnum.FALLBACK.getMessage());
        assertEquals("invalid argument", StatusEnum.VALIDATION_FAIL.getMessage());
        assertEquals("Failure", StatusEnum.FAIL.getMessage());
        assertEquals("Repeat login, log out an account please!", StatusEnum.REPEAT_LOGIN.getMessage());
        assertEquals("请求限流", StatusEnum.REQUEST_LIMIT.getMessage());
        assertEquals("You selected user is offline!, please try again later!", StatusEnum.OFF_LINE.getMessage());
    }

    /**
     * 测试枚举值码方法
     */
    @Test
    public void testCodeMethod() {
        assertEquals("9000", StatusEnum.SUCCESS.code());
        assertEquals("8000", StatusEnum.FALLBACK.code());
        assertEquals("4000", StatusEnum.FAIL.code());
    }

    /**
     * 测试枚举描述方法
     */
    @Test
    public void testMessageMethod() {
        assertEquals("Success", StatusEnum.SUCCESS.message());
        assertEquals("FALL_BACK", StatusEnum.FALLBACK.message());
    }

    /**
     * 测试通过枚举值码查找枚举值
     */
    @Test
    public void testFindStatusSuccess() {
        StatusEnum result = StatusEnum.findStatus("9000");
        assertEquals(StatusEnum.SUCCESS, result);
    }

    /**
     * 测试通过枚举值码查找枚举值
     */
    @Test
    public void testFindStatusFail() {
        StatusEnum result = StatusEnum.findStatus("4000");
        assertEquals(StatusEnum.FAIL, result);
    }

    /**
     * 测试通过枚举值码查找枚举值
     */
    @Test
    public void testFindStatusValidationFail() {
        StatusEnum result = StatusEnum.findStatus("3000");
        assertEquals(StatusEnum.VALIDATION_FAIL, result);
    }

    /**
     * 测试通过枚举值码查找枚举值
     */
    @Test
    public void testFindStatusIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            StatusEnum.findStatus("9999");
        });
    }

    /**
     * 测试通过枚举值码查找枚举值
     */
    @Test
    public void testFindStatusNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            StatusEnum.findStatus(null);
        });
    }

    /**
     * 测试获取全部枚举值
     */
    @Test
    public void testGetAllStatus() {
        List<StatusEnum> allStatus = StatusEnum.getAllStatus();

        assertNotNull(allStatus);
        assertFalse(allStatus.isEmpty());
        assertTrue(allStatus.contains(StatusEnum.SUCCESS));
        assertTrue(allStatus.contains(StatusEnum.FAIL));
        assertTrue(allStatus.contains(StatusEnum.FALLBACK));
    }

    /**
     * 测试获取全部枚举值码
     */
    @Test
    public void testGetAllStatusCode() {
        List<String> allCodes = StatusEnum.getAllStatusCode();

        assertNotNull(allCodes);
        assertFalse(allCodes.isEmpty());
        assertTrue(allCodes.contains("9000"));
        assertTrue(allCodes.contains("8000"));
        assertTrue(allCodes.contains("4000"));
        assertTrue(allCodes.contains("3000"));
    }

    /**
     * 测试离线消息枚举值
     */
    @Test
    public void testOfflineMessageEnums() {
        assertEquals("9200", StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR.getCode());
        assertEquals("9201", StatusEnum.OFFLINE_MESSAGE_FETCH_ERROR.getCode());
        assertEquals("9202", StatusEnum.OFFLINE_MESSAGE_DELETE_ERROR.getCode());
    }
}
