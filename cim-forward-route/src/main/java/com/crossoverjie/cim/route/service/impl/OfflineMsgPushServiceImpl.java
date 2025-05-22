package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.persistence.api.annotation.RedisLock;
import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.crossoverjie.cim.persistence.api.vo.req.SaveOfflineMsgReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.factory.OfflineMsgFactory;
import com.crossoverjie.cim.route.service.OfflineMsgPushService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.server.api.ServerApi;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class OfflineMsgPushServiceImpl implements OfflineMsgPushService {

    @Resource
    private OfflineMsgStore offlineMsgStore;

    @Resource
    private OfflineMsgFactory offlineMsgFactory;

    @Resource
    private UserInfoCacheService userInfoCacheService;

    @Resource
    private ServerApi serverApi;

    @Override
    @RedisLock(key = "T(java.lang.String).format('lock:offlineMsg:%s', #receiveUserId)",
            waitTime = 5, leaseTime = 30)
    public void fetchOfflineMsgs(CIMServerResVO cimServerResVO, Long receiveUserId) {

        String url = "http://" + cimServerResVO.getIp() + ":" + cimServerResVO.getHttpPort();

        List<OfflineMsg> offlineMsgs = offlineMsgStore.fetch(receiveUserId);
        if (offlineMsgs.isEmpty()) {
            return;
        }
        offlineMsgs.sort(Comparator.comparing(OfflineMsg::getCreatedAt));

        SendMsgReqVO msgReqVO = SendMsgReqVO
                .builder()
                .userId(receiveUserId)
                .cmd(BaseCommand.OFFLINE).batchMsg(offlineMsgs.stream().map(OfflineMsg::getContent).toList())
                .properties(offlineMsgs.get(0).getProperties())
                .build();

        serverApi.sendMsg(msgReqVO, url);

        //todo How to ensure that the message will definitely arrive
        offlineMsgStore.markDelivered(receiveUserId, offlineMsgs.stream().map(OfflineMsg::getMessageId).toList());

    }


    @RedisLock(key = "T(java.lang.String).format('lock:offlineMsg:%s', #p2pRequest.userId)",
            waitTime = 5, leaseTime = 30)
    @Override
    public void saveOfflineMsg(P2PReqVO p2pRequest) {

        Optional<CIMUserInfo> cimUserInfo = userInfoCacheService.loadUserInfoByUserId(p2pRequest.getUserId());

        cimUserInfo.ifPresent(userInfo -> {
            SaveOfflineMsgReqVO saveOfflineMsgReqVO = SaveOfflineMsgReqVO.builder()
                    .msg(p2pRequest.getMsg())
                    .receive_user_id(p2pRequest.getReceiveUserId())
                    .properties(Map.of(
                            Constants.MetaKey.SEND_USER_ID, cimUserInfo.get().getUserId().toString(),
                            Constants.MetaKey.SEND_USER_NAME, cimUserInfo.get().getUserName(),
                            Constants.MetaKey.RECEIVE_USER_ID, p2pRequest.getReceiveUserId().toString()
                    )).build();
            OfflineMsg offlineMsg = offlineMsgFactory.createFromVo(saveOfflineMsgReqVO);
            offlineMsgStore.save(offlineMsg);
        });
    }
}
