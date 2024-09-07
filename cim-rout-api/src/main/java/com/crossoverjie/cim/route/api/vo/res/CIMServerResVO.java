package com.crossoverjie.cim.route.api.vo.res;

import com.crossoverjie.cim.common.pojo.RouteInfo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 00:43
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CIMServerResVO implements Serializable {

    private String ip ;
    private Integer cimServerPort;
    private Integer httpPort;

}
