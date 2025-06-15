package com.crossoverjie.cim.route.api.vo.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 00:43
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CIMServerResVO implements Serializable {

    private String ip;
    private Integer cimServerPort;
    private Integer httpPort;
    private String authToken;

    public CIMServerResVO(String ip, Integer cimServerPort, Integer httpPort) {
        this.ip = ip;
        this.cimServerPort = cimServerPort;
        this.httpPort = httpPort;
    }
}
