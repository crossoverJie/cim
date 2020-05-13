package com.crossoverjie.cim.server.endpoint;

import com.crossoverjie.cim.server.util.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.util.Map;

/**
 * Function: 自定义端点监控
 *
 * @author crossoverJie
 *         Date: 17/04/2018 14:47
 * @since JDK 1.8
 */
public class CustomEndpoint extends AbstractEndpoint<Map<Long,NioSocketChannel>> {


    /**
     * 监控端点的 访问地址
     * @param id
     */
    public CustomEndpoint(String id) {
        //false 表示不是敏感端点
        super(id, false);
    }

    @Override
    public Map<Long, NioSocketChannel> invoke() {
        return SessionSocketHolder.getRelationShip();
    }
}
