package com.crossoverjie.cim.route.service.impl;

import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.vo.res.CIMServerResVO;
import com.crossoverjie.cim.route.vo.res.RegisterInfoResVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.crossoverjie.cim.route.constant.Constant.ACCOUNT_PREFIX;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 21:58
 * @since JDK 1.8
 */
@Service
public class AccountServiceRedisImpl implements AccountService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountServiceRedisImpl.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public RegisterInfoResVO register(RegisterInfoResVO info) {
        String key = ACCOUNT_PREFIX + info.getUserId();

        String name = redisTemplate.opsForValue().get(info.getUserName()) ;
        if (null == name){
            //为了方便查询，冗余存一份
            redisTemplate.opsForValue().set(key, info.getUserName());
            redisTemplate.opsForValue().set(info.getUserName(),key);
        }else {
            long userId = Long.parseLong(name.split(":")[1]);
            info.setUserId(userId);
            info.setUserName(info.getUserName());
        }

        return info ;
    }

    @Override
    public boolean login(LoginReqVO loginReqVO) throws Exception {
        String key = ACCOUNT_PREFIX + loginReqVO.getUserId();
        String userName = redisTemplate.opsForValue().get(key);
        if (null == userName){
            return false ;
        }

        if (!userName.equals(loginReqVO.getUserName())){
            return false ;
        }
        return true ;
    }

    @Override
    public void saveRouteInfo(CIMServerResVO vo) throws Exception {

    }
}
