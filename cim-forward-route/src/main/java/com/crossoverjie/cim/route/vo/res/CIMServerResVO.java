package com.crossoverjie.cim.route.vo.res;

import java.io.Serializable;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 00:43
 * @since JDK 1.8
 */
public class CIMServerResVO implements Serializable {

    private String ip ;
    private Integer port;

    public CIMServerResVO(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "CIMServerResVO{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
