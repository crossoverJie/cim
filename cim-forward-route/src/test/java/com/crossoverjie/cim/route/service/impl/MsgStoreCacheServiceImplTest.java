package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.service.MsgStoreService;
import com.crossoverjie.cim.route.vo.store.StoreMsgVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.crossoverjie.cim.route.constant.Constant.ACCOUNT_PREFIX;
import static com.crossoverjie.cim.route.constant.Constant.ROUTE_PREFIX;

@SpringBootTest(classes = RouteApplication.class)
@RunWith(SpringRunner.class)
public class MsgStoreCacheServiceImplTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    MsgStoreService msgStoreService;

    @Test
    public void testStoreMessage() {
        Set<String> keys = redisTemplate.keys(ACCOUNT_PREFIX + "*");
        List<Long> offLineUser = new ArrayList<>();
        for (String userId_prefix : keys) {
            String userId = userId_prefix.split(":")[1];
            if (redisTemplate.hasKey(ROUTE_PREFIX + userId))
                continue;
            offLineUser.add(Long.valueOf(userId));
            if (offLineUser.size() ==  2)
                break;
        }

        assert offLineUser.size() >= 1;

        //模拟发离线消息
        for (Long userId : offLineUser) {
            String message = "offlineMessage";

            //插入一条更早之前的消息
            String uuid = UUID.randomUUID().toString();
            StoreMsgVo storeMsgVo = new StoreMsgVo();
            storeMsgVo.setMsg(message);
            storeMsgVo.setSenderUserId(123L);
            storeMsgVo.setReceiverUserId(userId);
            storeMsgVo.setUuid(uuid);
            assert msgStoreService.putMessage(storeMsgVo);
            assert msgStoreService.addUserOffLineMessage(userId,uuid);

            uuid = UUID.randomUUID().toString();
            assert msgStoreService.putMessage(uuid,123L,userId,message);
            assert msgStoreService.addUserOffLineMessage(userId,uuid);

            //存在消息?
            assert msgStoreService.isMessageExist(uuid);

            //校验消息
            storeMsgVo = msgStoreService.getMessage(uuid);
            assert uuid.equals(storeMsgVo.getUuid());
            assert 123 == storeMsgVo.getSenderUserId();
            assert userId.equals(storeMsgVo.getReceiverUserId());
            assert "offlineMessage".equals(storeMsgVo.getMsg());

            //获取用户的未读消息
            List<String> userUnReadMsgList = msgStoreService.getUserOffLineMessageUUId(userId,1000);
            assert userUnReadMsgList.size() >= 2;

            //最后加入的消息，都存在list的最后面
            assert userUnReadMsgList.indexOf(uuid) == userUnReadMsgList.size() - 1;

            //设置最后添加的消息已读
            assert msgStoreService.setUserOffLineMessageRead(userId,uuid);

            //读完没有消息了
            userUnReadMsgList = msgStoreService.getUserOffLineMessageUUId(userId,10);
            assert userUnReadMsgList.size() == 0;
        }

    }

}