package com.crossoverjie.cim.route.service;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import com.crossoverjie.cim.route.kit.NetAddressIsReachable;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-12 21:40
 * @since JDK 1.8
 */
@Component
@Slf4j
public class CommonBizService {


    @Resource
    private RouteHandle routeHandle;

    /**
     * check ip and port, and return a new server if the current server is not available
     * @param routeInfo
     */
    @SneakyThrows
    public RouteInfo checkServerAvailable(RouteInfo routeInfo, String userId){
        boolean reachable = NetAddressIsReachable.checkAddressReachable(routeInfo.getIp(), routeInfo.getCimServerPort(), 1000);
        if (!reachable) {
            log.error("ip={}, port={} are not available, remove it.", routeInfo.getIp(), routeInfo.getCimServerPort());
            List<String> list = routeHandle.removeExpireServer(routeInfo);
            String routeServer = routeHandle.routeServer(list, userId);
            log.info("Reselect new server:[{}]", routeServer);
            return RouteInfoParseUtil.parse(routeServer);
        }
        return routeInfo;
    }
}
