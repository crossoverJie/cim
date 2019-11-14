package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.ChatGroupService;
import com.crossoverjie.cim.route.service.MsgStoreService;
import com.crossoverjie.cim.route.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.vo.res.RegisterInfoResVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.crossoverjie.cim.route.constant.Constant.GROUP_PREFIX;

/**
 * 群聊功能接口实现
 * 使用zset,按时间顺序分配群主
 *
 * @author georgeyang
 * Date: 2019/11/01 21:58
 * @since JDK 1.8
 */
@Service
public class ChatGroupServiceImpl implements ChatGroupService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChatGroupServiceImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MsgStoreService msgStoreService;


    @Override
    public Long createChatGroup(String chatGroupName, Long adminUserId, List<Long> userIds) throws Exception {
        if (chatGroupName == null || adminUserId == null || userIds == null || userIds.isEmpty())
            throw new InvalidParameterException("Invalid Parameter");
        long userId = System.currentTimeMillis();
        RegisterInfoResVO info = new RegisterInfoResVO(userId, chatGroupName);
        RegisterInfoResVO registerInfoResVO = accountService.register(info);
        Long chatGroupId = registerInfoResVO.getUserId();

        if (!chatGroupId.equals(userId))
            throw new CIMException(StatusEnum.ACCOUNT_EXIST);

        //入群
        userIds.remove(adminUserId);//移除群组不再前面的情况
        BoundZSetOperations<String, String> zSetOperations = redisTemplate.boundZSetOps(GROUP_PREFIX + chatGroupId);
        zSetOperations.add(String.valueOf(adminUserId), 0);
        userIds.forEach((item) -> {
            //入群先后顺序以list排列为准,后续逐个加入按时间(秒)记录
            zSetOperations.add(String.valueOf(item), zSetOperations.size());
        });

        return chatGroupId;
    }


    @Override
    public boolean isChatGroupExist(Long chatGroupId) {
        if (chatGroupId == null)
            return false;
        return redisTemplate.hasKey(GROUP_PREFIX + chatGroupId);
    }

    @Override
    public List<Long> getGroupMemberList(Long chatGroupId) {
        if (chatGroupId == null)
            return Collections.emptyList();
        BoundZSetOperations<String, String> zSetOperations = redisTemplate.boundZSetOps(GROUP_PREFIX + chatGroupId);
        int size = zSetOperations.size().intValue();
        if (size == 0)
            return Collections.emptyList();
        List<Long> retList = new LinkedList<Long>();
        Set<String> sort = zSetOperations.range(0, -1);
        sort.forEach((item) -> {
            retList.add(Long.valueOf(item));
        });
        return retList;
    }

    @Override
    public boolean addGroupMember(Long chatGroupId, Long userId) {
        if (chatGroupId == null || userId == null)
            return false;
        BoundZSetOperations<String, String> zSetOperations = redisTemplate.boundZSetOps(GROUP_PREFIX + chatGroupId);
        return zSetOperations.add(String.valueOf(userId), System.currentTimeMillis() / 1000d);
    }

    @Override
    public boolean isGroupAdmin(Long chatGroupId, Long userId) {
        BoundZSetOperations<String, String> zSetOperations = redisTemplate.boundZSetOps(GROUP_PREFIX + chatGroupId);
        Set<String> rangeTop = zSetOperations.range(0, 1);
        String top = rangeTop.iterator().next();
        return top.equals(String.valueOf(userId));
    }

    @Override
    public boolean deleteGroupMember(Long chatGroupId, Long userId) {
        if (chatGroupId == null || userId == null)
            return false;
        BoundZSetOperations<String, String> zSetOperations = redisTemplate.boundZSetOps(GROUP_PREFIX + chatGroupId);
        if (zSetOperations.size() <= 0)
            return false;//没有成员可以删了
        Long ret = zSetOperations.remove(String.valueOf(userId));
        return ret > 0;
    }

    @Override
    public boolean isGroupMemberExist(Long chatGroupId, Long userId) {
        if (chatGroupId == null || userId == null)
            return false;
        BoundZSetOperations<String, String> zSetOperations = redisTemplate.boundZSetOps(GROUP_PREFIX + chatGroupId);
        //确保值大于等于0
        Double score = zSetOperations.score(String.valueOf(userId));
        return score != null && score >= 0;
    }

    @Override
    public boolean dismissGroup(Long chatGroupId) {
        boolean hasKey = redisTemplate.hasKey(GROUP_PREFIX + chatGroupId);
        if (hasKey)
            redisTemplate.delete(GROUP_PREFIX + chatGroupId);
        return hasKey;
    }

    @Override
    public Integer sendGroupMessage(Long chatGroupId, Long senderId, String message) {
        return this.sendGroupMessageStore(false, chatGroupId, senderId, message);
    }

    @Override
    public Integer sendGroupMessageStore(boolean supportStore, Long chatGroupId, Long senderId, String message) {
        String msgUUID = "";
        if (supportStore) {
            msgUUID = UUID.randomUUID().toString();
            //先存一份消息详情,群发给群
            boolean putMsgSuccess = msgStoreService.putMessage(msgUUID, chatGroupId,chatGroupId, message);
            if (!putMsgSuccess) {
                return -1;
            }
        }

        if (chatGroupId == null || message == null)
            return -1;

        AtomicInteger receiveCount = new AtomicInteger();
        BoundZSetOperations<String, String> zSetOperations = redisTemplate.boundZSetOps(GROUP_PREFIX + chatGroupId);
        Set<String> sort = zSetOperations.range(0, -1);
        String finalMsgUUID = msgUUID;
        sort.forEach((item) -> {
            BaseResponse returnResponse = null;
            Long receiveUserId = Long.valueOf(item);
            try {
                //获取接收消息用户的路由信息
                CIMServerResVO cimServerResVO = accountService.loadRouteRelatedByUserId(receiveUserId);
                //推送消息
                String url = "http://" + cimServerResVO.getIp() + ":" + cimServerResVO.getHttpPort() + "/sendMsg";
                ChatReqVO chatVO = new ChatReqVO(receiveUserId, message);
                returnResponse = accountService.pushMsg(url, chatGroupId, chatVO);
                receiveCount.addAndGet(1);
            } catch (Exception e) {
                LOGGER.warn("发送失败:{}", item);
            }
            if (returnResponse == null || !returnResponse.isSuccess()) {
                if (supportStore) {
                    //用户不在线或发送失败，记录离线消息
                    boolean addUserOffLineMessage = msgStoreService.addUserOffLineMessage(receiveUserId, finalMsgUUID);
                    if (addUserOffLineMessage) {
                        receiveCount.addAndGet(1);
                    }
                }
            }
        });
        return receiveCount.get();
    }
}
