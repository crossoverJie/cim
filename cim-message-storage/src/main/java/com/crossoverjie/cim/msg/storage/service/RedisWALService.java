package com.crossoverjie.cim.msg.storage.service;

import com.crossoverjie.cim.msg.storage.pojo.OfflineMsg;

//todo Add batch modifications？
public interface RedisWALService {

    /**
     * Before writing: Write the offline message to redis
     *
     * @param msg
     */
    void logOfflineMessage(OfflineMsg msg);


    /**
     * Compensation: Background scheduled tasks, consumed from WAL and stored in the database
     */
    void startWALConsumer();
}
