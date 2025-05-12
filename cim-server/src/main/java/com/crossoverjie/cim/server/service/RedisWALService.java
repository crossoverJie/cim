package com.crossoverjie.cim.server.service;


import com.crossoverjie.cim.server.pojo.OfflineMsg;

import java.util.List;

//todo Add batch modifications？
public interface RedisWALService {

    /**
     * Before writing: Write the offline message to redis
     *
     * @param msg
     */
//    void logOfflineMsg(OfflineMsg msg);


    /**
     * Obtain all the offline messages of the user
     *
     * @param userId
     * @return
     */
    List<OfflineMsg> getOfflineMsgs(Long userId);

    /**
     * Compensation: Background scheduled tasks, consumed from WAL and stored in the database
     */
    void startOfflineMsgsWALConsumer();

    /**
     * Before writing: Write the offline message to redis
     * @param msg
     */
    void saveOfflineMsgToRedis(OfflineMsg msg);

    /**
     * Delete offline messages from redis
     * @param messageId
     */
    void deleteOfflineMsgFromRedis(String messageId);

    /**
     * Mark the message as delivered
     * @param messageId
     */
    void markDelivered(String messageId);


    /**
     * Migrate offline messages to the database
     * @param userId
     */
    void migrateOfflineMsgToDb(Long userId);
}
