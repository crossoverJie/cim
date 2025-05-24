package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.api.vo.res.RegisterInfoResVO;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.server.api.ServerApi;
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
    public Optional<CIMServerResVO> loadRouteRelatedByUserId(Long userId) {
        String value = redisTemplate.opsForValue().get(ROUTE_PREFIX + userId);

        if (value == null) {
            return Optional.empty();
        }

        RouteInfo parse = RouteInfoParseUtil.parse(value);
        CIMServerResVO cimServerResVO =
                new CIMServerResVO(parse.getIp(), parse.getCimServerPort(), parse.getHttpPort());
        return Optional.of(cimServerResVO);
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
    public void pushMsg(CIMServerResVO cimServerResVO, long sendUserId, ChatReqVO chatReqVO) {
        Optional<CIMUserInfo> cimUserInfo = userInfoCacheService.loadUserInfoByUserId(sendUserId);

        cimUserInfo.ifPresent(sendUserInfo -> {
            String url = "http://" + cimServerResVO.getIp() + ":" + cimServerResVO.getHttpPort();
            SendMsgReqVO vo =
                    new SendMsgReqVO(chatReqVO.getMsg(), chatReqVO.getUserId(), chatReqVO.getBatchMsg(), BaseCommand.MESSAGE);
            vo.setProperties(Map.of(
                    Constants.MetaKey.SEND_USER_ID, String.valueOf(sendUserId),
                    Constants.MetaKey.SEND_USER_NAME, sendUserInfo.getUserName(),
                    Constants.MetaKey.RECEIVE_USER_ID, String.valueOf(chatReqVO.getUserId()))
            );
            serverApi.sendMsg(vo, url);

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
