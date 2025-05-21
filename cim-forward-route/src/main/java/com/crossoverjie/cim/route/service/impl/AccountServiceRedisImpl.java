package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import com.crossoverjie.cim.persistence.api.annotation.RedisLock;
import com.crossoverjie.cim.persistence.api.pojo.OfflineMsg;
import com.crossoverjie.cim.persistence.api.service.OfflineMsgStore;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;
import com.crossoverjie.cim.route.constant.Constant;
import com.crossoverjie.cim.route.factory.OfflineMsgFactory;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.server.api.ServerApi;
import com.crossoverjie.cim.server.api.vo.req.OfflineMsgReqVO;
import com.crossoverjie.cim.server.api.vo.req.SaveOfflineMsgReqVO;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.crossoverjie.cim.common.enums.StatusEnum.OFF_LINE;
import static com.crossoverjie.cim.route.constant.Constant.*;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 21:58
 * @since JDK 1.8
 */
@Slf4j
@Service
public class AccountServiceRedisImpl implements AccountService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserInfoCacheService userInfoCacheService;

    @Resource
    private ServerApi serverApi;

    @Resource
    private OfflineMsgStore offlineMsgStore;

    @Resource
    private OfflineMsgFactory offlineMsgFactory;

    @Override
    public RegisterInfoResVO register(RegisterInfoResVO info) {
        String key = ACCOUNT_PREFIX + info.getUserId();

        String name = redisTemplate.opsForValue().get(info.getUserName());
        if (null == name) {
            //为了方便查询，冗余一份
            redisTemplate.opsForValue().set(key, info.getUserName());
            redisTemplate.opsForValue().set(info.getUserName(), key);
        } else {
            long userId = Long.parseLong(name.split(":")[1]);
            info.setUserId(userId);
            info.setUserName(info.getUserName());
        }

        return info;
    }

    @Override
    public StatusEnum login(LoginReqVO loginReqVO) throws Exception {
        //再去Redis里查询
        String key = ACCOUNT_PREFIX + loginReqVO.getUserId();
        String userName = redisTemplate.opsForValue().get(key);
        if (null == userName) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        if (!userName.equals(loginReqVO.getUserName())) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        //登录成功，保存登录状态
        boolean status = userInfoCacheService.saveAndCheckUserLoginStatus(loginReqVO.getUserId());
        if (!status) {
            //重复登录
            return StatusEnum.REPEAT_LOGIN;
        }

        return StatusEnum.SUCCESS;
    }

    @Override
    public void saveRouteInfo(LoginReqVO loginReqVO, String msg) throws Exception {
        String key = ROUTE_PREFIX + loginReqVO.getUserId();
        redisTemplate.opsForValue().set(key, msg);
    }

    @Override
    public Map<Long, CIMServerResVO> loadRouteRelated() {

        Map<Long, CIMServerResVO> routes = new HashMap<>(64);


        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        ScanOptions options = ScanOptions.scanOptions()
                .match(ROUTE_PREFIX + "*")
                .build();
        Cursor<byte[]> scan = connection.scan(options);

        while (scan.hasNext()) {
            byte[] next = scan.next();
            String key = new String(next, StandardCharsets.UTF_8);
            log.info("key={}", key);
            parseServerInfo(routes, key);

        }
        scan.close();

        return routes;
    }

    @Override
    public CIMServerResVO loadRouteRelatedByUserId(Long userId) {
        String value = redisTemplate.opsForValue().get(ROUTE_PREFIX + userId);

        if (value == null) {
            throw new CIMException(OFF_LINE);
        }

        RouteInfo parse = RouteInfoParseUtil.parse(value);
        CIMServerResVO cimServerResVO =
                new CIMServerResVO(parse.getIp(), parse.getCimServerPort(), parse.getHttpPort());
        return cimServerResVO;
    }

    private void parseServerInfo(Map<Long, CIMServerResVO> routes, String key) {
        long userId = Long.valueOf(key.split(":")[1]);
        String value = redisTemplate.opsForValue().get(key);
        RouteInfo parse = RouteInfoParseUtil.parse(value);
        CIMServerResVO cimServerResVO =
                new CIMServerResVO(parse.getIp(), parse.getCimServerPort(), parse.getHttpPort());
        routes.put(userId, cimServerResVO);
    }


    @Override
    public void pushMsg(CIMServerResVO cimServerResVO, long sendUserId, ChatReqVO groupReqVO) {
        Optional<CIMUserInfo> cimUserInfo = userInfoCacheService.loadUserInfoByUserId(sendUserId);

        cimUserInfo.ifPresentOrElse(userInfo -> {
            String url = getServerUrl(cimServerResVO);
            SendMsgReqVO vo =
                    new SendMsgReqVO(groupReqVO.getMsg(), groupReqVO.getUserId(), groupReqVO.getCmd());
            vo.setProperties(Map.of(
                    Constants.MetaKey.USER_ID, String.valueOf(sendUserId),
                    Constants.MetaKey.USER_NAME, userInfo.getUserName())
            );
            serverApi.sendMsg(vo, url);

        }, () -> {
            this.saveOfflineMsg(cimServerResVO, P2PReqVO.builder().userId(sendUserId).receiveUserId(groupReqVO.getUserId()).msg(groupReqVO.getMsg()).build());
        });
    }


    private String getServerUrl(CIMServerResVO cimServerResVO) {
        String url = "http://" + cimServerResVO.getIp() + ":" + cimServerResVO.getHttpPort();
        return url;
    }

    @Override
    public void sendOfflineMsgs(CIMServerResVO cimServerResVO, Long receiveUserId) {

        String url = getServerUrl(cimServerResVO);
//        serverApi.sendOfflineMsgs(OfflineMsgReqVO.builder().receiveUserId(receiveUserId).build(), url);
        this.fetchOfflineMsgs(receiveUserId, url);

    }

    @RedisLock(key = "T(java.lang.String).format('lock:offlineMsg:%s', #receiveUserId)",
            waitTime = 5, leaseTime = 30)
    public void fetchOfflineMsgs(Long receiveUserId, String url) {

        List<OfflineMsg> offlineMsgs = offlineMsgStore.fetch(receiveUserId);
        if (offlineMsgs.isEmpty()) {
            return;
        }
        offlineMsgs.sort(Comparator.comparing(OfflineMsg::getCreatedAt));

        for (OfflineMsg offlineMsg : offlineMsgs) {
            SendMsgReqVO sendMsgReqVO = new SendMsgReqVO(offlineMsg.getContent(), offlineMsg.getUserId(), BaseCommand.OFFLINE, offlineMsg.getProperties());
            // todo sendMsg中的requestId是userId，这个会不会有问题
            serverApi.sendMsg(sendMsgReqVO, url);
        }

        //todo How to ensure that the message will definitely arrive
        offlineMsgStore.markDelivered(receiveUserId, offlineMsgs.stream().map(OfflineMsg::getMessageId).toList());
    }


    @RedisLock(key = "T(java.lang.String).format('lock:offlineMsg:%s', #vo.userId)",
            waitTime = 5, leaseTime = 30)
    @Override
    public void saveOfflineMsg(CIMServerResVO cimServerResVO, P2PReqVO p2pRequest) {

        Optional<CIMUserInfo> cimUserInfo = userInfoCacheService.loadUserInfoByUserId(p2pRequest.getReceiveUserId());

        cimUserInfo.ifPresent(userInfo -> {
            SaveOfflineMsgReqVO saveOfflineMsgReqVO = SaveOfflineMsgReqVO.builder()
                    .msg(p2pRequest.getMsg())
                    .userId(p2pRequest.getReceiveUserId())
                    .properties(Map.of(
                            Constants.MetaKey.USER_NAME, userInfo.getUserName())
                    ).build();
            OfflineMsg offlineMsg = offlineMsgFactory.createFromVo(saveOfflineMsgReqVO);
            offlineMsgStore.save(offlineMsg);
        });

    }

    @Override
    public void offLine(Long userId) {

        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/offLine.lua")));

        redisTemplate.execute(redisScript,
                Collections.singletonList(ROUTE_PREFIX + userId),
                LOGIN_STATUS_PREFIX,
                userId.toString());
    }
}
