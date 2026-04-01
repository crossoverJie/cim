package com.crossoverjie.cim.common.util;

import com.crossoverjie.cim.common.pojo.RouteInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        RouteInfo originalInfo = new RouteInfo("2001:0db8:85a3:0000:0000:8a2e:0370:7334", 8080, 9090);
        String str = RouteInfoParseUtil.parse(originalInfo);

        assertTrue(str.contains("2001:0db8"), "序列化后的字符串必须包含 IPv6 地址");

        try {
            RouteInfo parsedInfo = RouteInfoParseUtil.parse(str);
            assertNotEquals(originalInfo.getIp(), parsedInfo.getIp(),
                    "虽然解析成功了，但 IPv6 地址被截断或修改了，数据不一致！");

            fail("预期会解析失败或数据不一致，但实际完全成功了，这不符合预期。");

        } catch (Exception e) {
            System.out.println("成功捕获到预期异常: " + e.getClass().getName() + " - " + e.getMessage());
        }
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
