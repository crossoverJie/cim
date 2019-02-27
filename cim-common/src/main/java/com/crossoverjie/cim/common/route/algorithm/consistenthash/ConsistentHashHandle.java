package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import com.crossoverjie.cim.common.route.algorithm.RouteHandle;

import java.util.List;

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
}
