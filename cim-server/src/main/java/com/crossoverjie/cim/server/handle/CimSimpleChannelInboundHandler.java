package com.crossoverjie.cim.server.handle;

import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.server.kit.RouteHandler;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class CimSimpleChannelInboundHandler<T> extends SimpleChannelInboundHandler<T> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(CimSimpleChannelInboundHandler.class);


    private RouteHandler getRouteHandler() {
        RouteHandler routeHandler = SpringBeanFactory.getBean("routeHandler", RouteHandler.class);
        return routeHandler;
    }


    /**
     * 用户下线
     * @param userInfo
     * @param channel
     * @throws IOException
     */
    protected void userOffLine(CIMUserInfo userInfo, NioSocketChannel channel) throws IOException {
        getRouteHandler().userOffLine(userInfo,channel);
    }

    /**
     * 下线，清除路由关系
     *
     * @param userInfo
     * @throws IOException
     */
    protected void clearRouteInfo(CIMUserInfo userInfo) throws IOException {
        getRouteHandler().clearRouteInfo(userInfo);
    }
}
