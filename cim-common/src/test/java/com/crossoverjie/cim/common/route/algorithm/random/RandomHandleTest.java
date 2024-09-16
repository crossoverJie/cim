package com.crossoverjie.cim.common.route.algorithm.random;

import static org.junit.jupiter.api.Assertions.*;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.route.algorithm.loop.LoopHandle;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RandomHandleTest {
    @Test
    void removeExpireServer() {
        RouteHandle routeHandle = new RandomHandle();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var routeInfo = new RouteInfo("127.0.0." + i, 1000, 2000);
            strings.add(RouteInfoParseUtil.parse(routeInfo));
        }
        routeHandle.routeServer(strings, "zs");

        RouteInfo remove = new RouteInfo("127.0.0.0", 1000, 2000);
        List<String> list = routeHandle.removeExpireServer(remove);
        boolean contains = list.contains(RouteInfoParseUtil.parse(remove));
        assertFalse(contains);
    }

}