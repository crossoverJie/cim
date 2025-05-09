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
    void startWALConsumer();

    void saveOfflineMsgToWal(OfflineMsg msg);

    void deleteOfflineMsgFromWal(String messageId);
}
