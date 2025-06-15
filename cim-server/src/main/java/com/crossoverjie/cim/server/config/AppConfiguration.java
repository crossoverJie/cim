package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.common.enums.RegistryType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/8/24 01:43
 * @since JDK 1.8
 */
@Component
public class AppConfiguration {

    @Value("${app.zk.root}")
    private String zkRoot;

    @Value("${app.zk.addr}")
    private String zkAddr;

    @Value("${cim.server.port}")
    private int cimServerPort;

    @Value("${cim.route.url}")
    private String routeUrl;

    /**
     * 链接服务端注册类型
     * <p>
     * no: 不注册（客户端直连模式
     * zk: zookeeper 存储
     */
    @Value("${register.type:no}")
    private RegistryType registerType;

    public String getRouteUrl() {
        return routeUrl;
    }

    @Value("${cim.heartbeat.time}")
    private long heartBeatTime;

    @Value("${app.zk.connect.timeout}")
    private int zkConnectTimeout;


    public void setRouteUrl(String routeUrl) {
        this.routeUrl = routeUrl;
    }

    public int getZkConnectTimeout() {
        return zkConnectTimeout;
    }

    public String getZkRoot() {
        return zkRoot;
    }

    public void setZkRoot(String zkRoot) {
        this.zkRoot = zkRoot;
    }

    public String getZkAddr() {
        return zkAddr;
    }

    public void setZkAddr(String zkAddr) {
        this.zkAddr = zkAddr;
    }


    public int getCimServerPort() {
        return cimServerPort;
    }

    public void setCimServerPort(int cimServerPort) {
        this.cimServerPort = cimServerPort;
    }

    public long getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    public RegistryType getRegisterType() {
        return registerType;
    }

    public AppConfiguration setRegisterType(RegistryType registerType) {
        this.registerType = registerType;
        return this;
    }

    public AppConfiguration setZkConnectTimeout(int zkConnectTimeout) {
        this.zkConnectTimeout = zkConnectTimeout;
        return this;
    }
}
