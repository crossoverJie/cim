package com.crossoverjie.cim.common.util;

import com.crossoverjie.cim.common.pojo.RouteInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Function:
 * Date: 01/04/2026 20:00
 */
public class RouteInfoParseUtilTest {


    /**
     * 测试最大端口号
     */
    @Test
    public void testParseMaxPortNumber() {
        String info = "127.0.0.1:65535:65535";
        RouteInfo routeInfo = RouteInfoParseUtil.parse(info);
        assertEquals(65535, routeInfo.getCimServerPort());
        assertEquals(65535, routeInfo.getHttpPort());
    }

    /**
     * 测试最小端口号
     */
    @Test
    public void testParseMinPortNumber() {
        String info = "127.0.0.1:0:0";
        RouteInfo routeInfo = RouteInfoParseUtil.parse(info);
        assertEquals(0, routeInfo.getCimServerPort());
        assertEquals(0, routeInfo.getHttpPort());
    }

    /**
     * 正常解析
     */
    @Test
    public void testParseToNormalRouteInfo() {
        RouteInfo routeInfo = new RouteInfo("192.168.1.1", 8080, 8081);
        String result = RouteInfoParseUtil.parse(routeInfo);
        assertEquals("192.168.1.1:8080:8081", result);
    }

    /**
     * 测试特殊字符
     */
    @Test
    public void testParseToSpecialCharacters() {
        RouteInfo routeInfo = new RouteInfo("2001:db8::1", 8080, 8081);
        String result = RouteInfoParseUtil.parse(routeInfo);
        assertEquals("2001:db8::1:8080:8081", result);
    }

    /**
     * 测试转换回原字符串
     */
    @Test
    public void testRoundTrip() {
        String original = "192.168.1.1:8080:8081";
        RouteInfo routeInfo = RouteInfoParseUtil.parse(original);
        String result = RouteInfoParseUtil.parse(routeInfo);
        assertEquals(original, result);
    }


}
