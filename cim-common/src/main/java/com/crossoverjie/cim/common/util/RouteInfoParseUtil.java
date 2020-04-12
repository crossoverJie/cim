package com.crossoverjie.cim.common.util;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.RouteInfo;

import static com.crossoverjie.cim.common.enums.StatusEnum.VALIDATION_FAIL;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-12 20:42
 * @since JDK 1.8
 */
public class RouteInfoParseUtil {

    public static RouteInfo parse(String info){
        try {
            String[] serverInfo = info.split(":");
            RouteInfo routeInfo =  new RouteInfo(serverInfo[0], Integer.parseInt(serverInfo[1]),Integer.parseInt(serverInfo[2])) ;
            return routeInfo ;
        }catch (Exception e){
            throw new CIMException(VALIDATION_FAIL) ;
        }
    }
}
