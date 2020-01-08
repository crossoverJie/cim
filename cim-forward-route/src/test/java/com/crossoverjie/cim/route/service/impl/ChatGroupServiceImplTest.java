package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.route.RouteApplication;
import com.crossoverjie.cim.route.service.ChatGroupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.crossoverjie.cim.route.constant.Constant.ROUTE_PREFIX;

@SpringBootTest(classes = RouteApplication.class)
@RunWith(SpringRunner.class)
public class ChatGroupServiceImplTest {
    @Autowired
    private ChatGroupService chatGroupService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testChatGroup() throws Exception {
        //测试不存在的群组
        assert chatGroupService.isChatGroupExist(0L) == false;

        String testChatRoomAccount = System.currentTimeMillis() + "@chatroom";

        List<Long> userIds = new ArrayList<>();
        userIds.add(-123L);//第一个群主
        userIds.add(-1236L);//第二个群成员，第一个预备群主
        //创建群测试
        Long chatGroupId = chatGroupService.createChatGroup(testChatRoomAccount, -123L, userIds);
        assert chatGroupId > 0;

        //群主检查
        assert chatGroupService.isGroupAdmin(chatGroupId, -123L);
        assert !chatGroupService.isGroupAdmin(chatGroupId, -1236L);

        //群存在?
        assert chatGroupService.isChatGroupExist(chatGroupId);

        //获取群成员
        userIds = chatGroupService.getGroupMemberList(chatGroupId);
        assert userIds.size() == 2;

        //增加新成员(在线)
        List<Long> uids = loadOnlineUser();
        assert uids != null && !uids.isEmpty();
        for (Long id : uids) {
            assert chatGroupService.addGroupMember(chatGroupId, id);
        }

        //不能增加已有成员
        assert !chatGroupService.addGroupMember(chatGroupId, -123L);

        //删除成员
        assert chatGroupService.deleteGroupMember(chatGroupId, -123L);

        //群组走了，预备群主上位
        assert chatGroupService.isGroupAdmin(chatGroupId, -1236L);

        //删除后再检查他的确离开了
        assert !chatGroupService.isGroupMemberExist(chatGroupId, -123L);

        //群发信息，有在线成员，一定能发出去
        assert !chatGroupService.sendGroupMessage(chatGroupId, uids.get(0), "大家好呀").isEmpty();

        //解散群
        assert chatGroupService.dismissGroup(chatGroupId);

        //群还在吗
        assert !chatGroupService.isChatGroupExist(chatGroupId);

        //不存在的群，还拉取成员
        userIds = chatGroupService.getGroupMemberList(chatGroupId);
        assert userIds == null || userIds.isEmpty();

        //避免重新创建
        Long newChatGroupId = null;
        try {
            newChatGroupId = chatGroupService.createChatGroup(testChatRoomAccount, -1234L, userIds);
        } catch (Exception e) {
        }
        assert newChatGroupId == null;
    }

    private List<Long> loadOnlineUser() {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        ScanOptions options = ScanOptions.scanOptions()
                .match(ROUTE_PREFIX + "*")
                .count(10)
                .build();
        Cursor<byte[]> scan = connection.scan(options);
        List<Long> uidList = new ArrayList<>();
        while (scan.hasNext()) {
            byte[] next = scan.next();
            String key = new String(next, StandardCharsets.UTF_8);
            String idStr = key.replace(ROUTE_PREFIX, "");
            Long id = Long.valueOf(idStr);
            uidList.add(id);
        }
        return uidList;
    }
}