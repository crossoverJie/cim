package com.crossoverjie.cim.route.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.route.service.MsgStoreService;
import com.crossoverjie.cim.route.vo.store.StoreMsgVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.crossoverjie.cim.route.constant.Constant.MESSAGE_PREFIX;
import static com.crossoverjie.cim.route.constant.Constant.OFFLINE_MESSAGE_PREFIX;

@Service
public class MsgStoreCacheServiceImpl implements MsgStoreService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public StoreMsgVo getMessage(String uuid) {
        String storeMsgJson = redisTemplate.opsForValue().get(MESSAGE_PREFIX + uuid);
        return JSONObject.parseObject(storeMsgJson,StoreMsgVo.class);
    }

    @Override
    public boolean isMessageExist(String uuid) {
        return redisTemplate.hasKey(MESSAGE_PREFIX + uuid);
    }

    @Override
    public boolean putMessage(String uuid, Long senderUserId, Long receiverUserId, String msg) {
        if (uuid == null || senderUserId == null || receiverUserId == null || msg == null)
            return false;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid",uuid);
        jsonObject.put("senderUserId",senderUserId);
        jsonObject.put("receiverUserId",receiverUserId);
        jsonObject.put("msg",msg);
        String value = jsonObject.toJSONString();
        //默认保留31天
        redisTemplate.opsForValue().set(MESSAGE_PREFIX + uuid, value, 31, TimeUnit.DAYS);
        return true;
    }

    @Override
    public boolean putMessage(StoreMsgVo storeMsgVo) {
        if (storeMsgVo == null || storeMsgVo.getUuid() == null)
            return false;
        String value = JSONObject.toJSONString(storeMsgVo);
        //默认保留31天
        redisTemplate.opsForValue().set(MESSAGE_PREFIX + storeMsgVo.getUuid(), value, 31, TimeUnit.DAYS);
        return true;
    }

    @Override
    public boolean addUserOffLineMessage(Long userId, String uuid) {
        if (userId == null || uuid == null)
            return false;
        BoundListOperations<String,String> boundListOperations = redisTemplate.boundListOps(OFFLINE_MESSAGE_PREFIX + userId);
        Long ret = boundListOperations.rightPush(uuid);
        //0开始压入，到5，顺序是 0,1,2,3,4,5
        return ret != null && ret >= 0;
    }

    @Override
    public List<String> getUserOffLineMessageUUId(Long userId, int limit) {
        BoundListOperations<String,String> boundListOperations = redisTemplate.boundListOps(OFFLINE_MESSAGE_PREFIX + userId);
        List<String> msgIds = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            if (i >= boundListOperations.size())
                break;
            String uuid = boundListOperations.index(i);
            msgIds.add(uuid);
        }
        return msgIds;
    }

    @Override
    public boolean setUserOffLineMessageRead(Long userId, String uuidRead) {
        if (userId == null || uuidRead == null)
            return false;
        BoundListOperations<String,String> boundListOperations = redisTemplate.boundListOps(OFFLINE_MESSAGE_PREFIX + userId);
        int findIndex = -1;
        for (int i = 0; i < boundListOperations.size(); i++) {
            String msgId = boundListOperations.index(i);
            if (uuidRead.equals(msgId)) {
                findIndex = i;
                break;
            }
        }

        if (findIndex >= 0){
            for (int i = 0; i <= findIndex; i++) {
                boundListOperations.leftPop(7,TimeUnit.DAYS);
            }
        }

        return findIndex >= 0;
    }

}
