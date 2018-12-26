package com.crossoverjie.cim.route.service;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.metastore.MetaStore;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.route.kit.NetAddressIsReachable;
import jakarta.annotation.Resource;
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
    private MetaStore metaStore ;

    /**
     * check ip and port
     * @param routeInfo
     */
    @SneakyThrows
    public void checkServerAvailable(RouteInfo routeInfo){
        boolean reachable = NetAddressIsReachable.checkAddressReachable(routeInfo.getIp(), routeInfo.getCimServerPort(), 1000);
        if (!reachable) {
            log.error("ip={}, port={} are not available", routeInfo.getIp(), routeInfo.getCimServerPort());
            metaStore.rebuildCache();
            throw new CIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }

    }
}
