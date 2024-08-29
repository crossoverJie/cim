package com.crossoverjie.cim.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-12 20:48
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@Builder
public final class RouteInfo {

    private String ip ;
    private Integer cimServerPort;
    private Integer httpPort;
}
