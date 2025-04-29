package com.crossoverjie.cim.msg.storage.service;

import com.crossoverjie.cim.msg.storage.pojo.OfflineMsg;

//todo Add batch modifications？
public interface RedisWALService {

    /**
     * 写前：将离线消息写入 Redis Stream
     *
     * @param msg
     */
    void logOfflineMessage(OfflineMsg msg);

    /**
     * 将已落库的离线消息删除
     */
    void deleteAckedMessages(OfflineMsg msgs);

    /**
     * 补偿：后台定时任务，从 WAL 中消费并落库
     */
    void startWALConsumer();
}
