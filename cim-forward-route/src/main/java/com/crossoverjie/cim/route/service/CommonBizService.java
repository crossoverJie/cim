package com.crossoverjie.cim.route.service;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.route.kit.NetAddressIsReachable;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-12 21:40
 * @since JDK 1.8
 */
@Component
public class CommonBizService {

    /**
     * check ip and port
     * @param routeInfo
     */
    public void checkServerAvailable(RouteInfo routeInfo){
        boolean reachable = NetAddressIsReachable.checkAddressReachable(routeInfo.getIp(), routeInfo.getCimServerPort(), 1000);
        if (!reachable) {
            throw new CIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }

    }
}
