package com.crossoverjie.cim.client.sdk.vo.res;

import java.io.Serializable;
import lombok.Data;

@Data
public class ServerResVO implements Serializable {

    /**
     * code : 9000
     * message : success
     * reqNo : null
     * dataBody : {"ip":"127.0.0.1","port":8081}
     */

    private String code;
    private String message;
    private Object reqNo;
    private ServerInfo dataBody;

    @Data
    public static class ServerInfo {
        /**
         * ip : 127.0.0.1
         * port : 8081
         */
        private String ip ;
        private Integer cimServerPort;
        private Integer httpPort;

    }

}
