package com.crossoverjie.cim.common.route.algorithm.loop;

import static org.junit.jupiter.api.Assertions.*;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoopHandleTest {

    @Test
    void removeExpireServer() {
        RouteHandle routeHandle = new LoopHandle();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var routeInfo = new RouteInfo("127.0.0." + i, 1000, 2000);
            strings.add(RouteInfoParseUtil.parse(routeInfo));
        }
        String zs = routeHandle.routeServer(strings, "zs");
        String zs2 = routeHandle.routeServer(strings, "zs");
        assertNotEquals(zs, zs2);

        RouteInfo remove = new RouteInfo("127.0.0.0", 1000, 2000);
        List<String> list = routeHandle.removeExpireServer(remove);
        boolean contains = list.contains(RouteInfoParseUtil.parse(remove));
        assertFalse(contains);
    }
}