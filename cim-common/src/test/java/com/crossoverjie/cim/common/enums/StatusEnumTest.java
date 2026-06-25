package com.crossoverjie.cim.common.enums;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Function: Test StatusEnum enum class
 * Date: 01/04/2026 20:00
 */
public class StatusEnumTest {

    /**
     * Test enum values exist
     */
    @Test
    public void testEnumValues() {
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
     * Test enum codes
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
     * Test enum messages
     */
    @Test
    public void testGetMessage() {
        assertEquals("Success", StatusEnum.SUCCESS.getMessage());
        assertEquals("FALL_BACK", StatusEnum.FALLBACK.getMessage());
        assertEquals("invalid argument", StatusEnum.VALIDATION_FAIL.getMessage());
        assertEquals("Failure", StatusEnum.FAIL.getMessage());
        assertEquals("Repeat login, log out an account please!", StatusEnum.REPEAT_LOGIN.getMessage());
        assertEquals("Request rate limited", StatusEnum.REQUEST_LIMIT.getMessage());
        assertEquals("You selected user is offline!, please try again later!", StatusEnum.OFF_LINE.getMessage());
    }

    /**
     * Test code() method
     */
    @Test
    public void testCodeMethod() {
        assertEquals("9000", StatusEnum.SUCCESS.code());
        assertEquals("8000", StatusEnum.FALLBACK.code());
        assertEquals("4000", StatusEnum.FAIL.code());
    }

    /**
     * Test message() method
     */
    @Test
    public void testMessageMethod() {
        assertEquals("Success", StatusEnum.SUCCESS.message());
        assertEquals("FALL_BACK", StatusEnum.FALLBACK.message());
    }

    /**
     * Test findStatus success
     */
    @Test
    public void testFindStatusSuccess() {
        StatusEnum result = StatusEnum.findStatus("9000");
        assertEquals(StatusEnum.SUCCESS, result);
    }

    /**
     * Test findStatus fail
     */
    @Test
    public void testFindStatusFail() {
        StatusEnum result = StatusEnum.findStatus("4000");
        assertEquals(StatusEnum.FAIL, result);
    }

    /**
     * Test findStatus validation fail
     */
    @Test
    public void testFindStatusValidationFail() {
        StatusEnum result = StatusEnum.findStatus("3000");
        assertEquals(StatusEnum.VALIDATION_FAIL, result);
    }

    /**
     * Test findStatus illegal argument
     */
    @Test
    public void testFindStatusIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            StatusEnum.findStatus("9999");
        });
    }

    /**
     * Test findStatus null
     */
    @Test
    public void testFindStatusNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            StatusEnum.findStatus(null);
        });
    }

    /**
     * Test get all enum values
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
     * Test get all enum codes
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
     * Test offline message enums
     */
    @Test
    public void testOfflineMessageEnums() {
        assertEquals("9200", StatusEnum.OFFLINE_MESSAGE_STORAGE_ERROR.getCode());
        assertEquals("9201", StatusEnum.OFFLINE_MESSAGE_FETCH_ERROR.getCode());
        assertEquals("9202", StatusEnum.OFFLINE_MESSAGE_DELETE_ERROR.getCode());
    }
}
