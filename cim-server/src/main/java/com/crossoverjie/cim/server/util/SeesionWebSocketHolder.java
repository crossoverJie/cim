package com.crossoverjie.cim.server.util;

import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket的session管理:
 *
 * @author georgeyang
 *         Date: 05/11/2019 12:33
 * @since JDK 1.8
 */
public class SeesionWebSocketHolder {
    private static final Map<Long, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>(16);
    private static final Map<Long, String> SESSION_MAP = new ConcurrentHashMap<>(16);
    private static final Map<String, WebSocketServerHandshaker> webSocketHandshakerMap = new ConcurrentHashMap<String, WebSocketServerHandshaker>();


    public static void saveSession(Long userId,String userName){
        SESSION_MAP.put(userId, userName);
    }

    public static void removeSession(Long userId){
        SESSION_MAP.remove(userId) ;
    }

    public static void put(Long id, NioSocketChannel socketChannel) {
        CHANNEL_MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(Long id) {
        return CHANNEL_MAP.get(id);
    }

    public static Map<Long, NioSocketChannel> getMAP() {
        return CHANNEL_MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> CHANNEL_MAP.remove(entry.getKey()));
    }

    /**
     * 获取注册用户信息
     * @param nioSocketChannel
     * @return
     */
    public static CIMUserInfo getUserId(NioSocketChannel nioSocketChannel){
        for (Map.Entry<Long, NioSocketChannel> entry : CHANNEL_MAP.entrySet()) {
            NioSocketChannel value = entry.getValue();
            if (nioSocketChannel == value){
                Long key = entry.getKey();
                String userName = SESSION_MAP.get(key);
                CIMUserInfo info = new CIMUserInfo(key,userName) ;
                return info ;
            }
        }

        return null;
    }

    public static WebSocketServerHandshaker getWebSocketServerHandshaker(String longText) {
        return webSocketHandshakerMap.get(longText);
    }

    public static void putWebSocketServerHandshaker(String longText,WebSocketServerHandshaker serverHandshaker) {
        webSocketHandshakerMap.put(longText,serverHandshaker);
    }

}
