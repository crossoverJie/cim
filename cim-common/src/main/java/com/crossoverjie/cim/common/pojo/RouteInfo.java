package com.crossoverjie.cim.common.pojo;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-12 20:48
 * @since JDK 1.8
 */
public final class RouteInfo {

    private String ip ;
    private Integer cimServerPort;
    private Integer httpPort;

    public RouteInfo(String ip, Integer cimServerPort, Integer httpPort) {
        this.ip = ip;
        this.cimServerPort = cimServerPort;
        this.httpPort = httpPort;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getCimServerPort() {
        return cimServerPort;
    }

    public void setCimServerPort(Integer cimServerPort) {
        this.cimServerPort = cimServerPort;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }
}
