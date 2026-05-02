package com.crossoverjie.cim.client.sdk.io;

import com.crossoverjie.cim.client.sdk.Client;
import java.util.Map;

public interface MessageListener {

    /**
     * @param client     client
     * @param properties meta data
     * @param msg        msgs
     */
    void received(Client client, Map<String, String> properties, String msg);

    /**
     * 消息已读状态更新回调
     * @param client client
     * @param msgId 消息ID
     * @param readUserId 已读用户ID
     * @param readUserName 已读用户名
     * @param readTimestamp 已读时间戳
     */
    default void onReadStatusUpdate(Client client, long msgId, long readUserId, String readUserName, long readTimestamp) {
    }
}
