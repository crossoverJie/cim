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
     * Server port
     */
    @Value("${cim.server.port}")
    private int nettyPort;

    /**
     * Server host IP address
     */
    private String host;

    @PostConstruct
    public void init() throws Exception {
        this.host = getLocalHostAddress();
        log.info("Server config, host={}, port={}", host, nettyPort);
    }

    /**
     * Get local LAN IP address
     * Prefer non-loopback, non-virtual IPv4 addresses
     */
    private String getLocalHostAddress() throws Exception {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // Skip loopback and down interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    String hostAddress = address.getHostAddress();

                    // Skip IPv6 addresses
                    if (hostAddress.contains(":")) {
                        continue;
                    }

                    // Skip 127.0.0.1
                    if (!hostAddress.equals("127.0.0.1")) {
                        log.debug("Found network interface: {}, address: {}", networkInterface.getName(), hostAddress);
                        return hostAddress;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get network interface address", e);
        }

        // Fallback for container environments
        String fallbackHost = InetAddress.getLocalHost().getHostAddress();
        log.warn("Using fallback host address: {}", fallbackHost);
        return fallbackHost;
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
