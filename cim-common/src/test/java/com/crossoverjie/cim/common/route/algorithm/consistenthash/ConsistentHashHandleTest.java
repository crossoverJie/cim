package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import static org.junit.jupiter.api.Assertions.*;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConsistentHashHandleTest {

    @Test
    void removeSortMapExpireServer() {
        ConsistentHashHandle routeHandle = new ConsistentHashHandle();
        routeHandle.setHash(new SortArrayMapConsistentHash());
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var routeInfo = new RouteInfo("127.0.0." + i, 1000, 2000);
            strings.add(RouteInfoParseUtil.parse(routeInfo));
        }
        RouteInfo routeInfo = new RouteInfo("127.0.0.9", 1000, 2000);
        String parse = RouteInfoParseUtil.parse(routeInfo);
        String r1 = routeHandle.routeServer(strings, parse);
        String r2 = routeHandle.routeServer(strings, parse);
        assertEquals(r1, r2);

        List<String> list = routeHandle.removeExpireServer(routeInfo);
        boolean contains = list.contains(parse);
        assertFalse(contains);
    }
    @Test
    void removeTreeMapExpireServer() {
        ConsistentHashHandle routeHandle = new ConsistentHashHandle();
        routeHandle.setHash(new TreeMapConsistentHash());
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var routeInfo = new RouteInfo("127.0.0." + i, 1000, 2000);
            strings.add(RouteInfoParseUtil.parse(routeInfo));
        }
        RouteInfo routeInfo = new RouteInfo("127.0.0.9", 1000, 2000);
        String parse = RouteInfoParseUtil.parse(routeInfo);
        String r1 = routeHandle.routeServer(strings, parse);
        String r2 = routeHandle.routeServer(strings, parse);
        assertEquals(r1, r2);

        List<String> list = routeHandle.removeExpireServer(routeInfo);
        boolean contains = list.contains(parse);
        assertFalse(contains);
    }
}