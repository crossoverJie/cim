package com.crossoverjie.cim.server.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author chenqwwq
 * @date 2026/4/9
 **/
@Slf4j
@Configuration
public class ServerConfig {

    /**
     * 当前服务的地址
     */
    @Value("${cim.server.port}")
    private int nettyPort;

    /**
     * 当前服务的ip地址
     */
    private String host;

    @PostConstruct
    public void init() throws Exception {
        this.host = getLocalHostAddress();
        log.info("当前服务配置, host={}, port={}", host, nettyPort);
    }

    /**
     * 获取本机局域网 IP 地址
     * 优先获取非回环、非虚拟的 IPv4 地址
     */
    private String getLocalHostAddress() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                continue;
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address.getHostAddress().contains(":")) {
                    continue;
                }
                return address.getHostAddress();
            }
        }
        return InetAddress.getLocalHost().getHostAddress();
    }

    public String getHost() {
        return host;
    }

    public ServerConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getNettyPort() {
        return nettyPort;
    }

    public ServerConfig setNettyPort(int nettyPort) {
        this.nettyPort = nettyPort;
        return this;
    }
}
