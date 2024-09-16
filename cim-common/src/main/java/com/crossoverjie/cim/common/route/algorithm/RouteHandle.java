package com.crossoverjie.cim.common.route.algorithm;

import com.crossoverjie.cim.common.pojo.RouteInfo;
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
    // TODO: 2024/9/13 Use List<RouteInfo> instead of List<String> to make the code more type-safe
    String routeServer(List<String> values,String key) ;

    List<String> removeExpireServer(RouteInfo routeInfo);
}
