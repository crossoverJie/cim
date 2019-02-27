package com.crossoverjie.cim.common.route.algorithm;

import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-02-27 00:31
 * @since JDK 1.8
 */
public interface RouteHandle {

    /**
     * 再一批服务器里进行路由
     * @param values
     * @param key
     * @return
     */
    String routeServer(List<String> values,String key) ;
}
