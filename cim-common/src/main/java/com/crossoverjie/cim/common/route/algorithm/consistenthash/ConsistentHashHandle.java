package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;

import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-02-27 00:33
 * @since JDK 1.8
 */
public class ConsistentHashHandle implements RouteHandle {
    private AbstractConsistentHash hash ;

    public void setHash(AbstractConsistentHash hash) {
        this.hash = hash;
    }

    @Override
    public String routeServer(List<String> values, String key) {
        return hash.process(values, key);
    }

    @Override
    public List<String> removeExpireServer(RouteInfo routeInfo) {
        Map<String, String> remove = hash.remove(RouteInfoParseUtil.parse(routeInfo));
        return new ArrayList<>(remove.keySet());
    }
}
