package com.crossoverjie.cim.common.auth;

import com.crossoverjie.cim.common.auth.jwt.dto.PayloadVO;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author chenqwwq
 * @date 2025/8/7
 **/
public class JwtAuthTest {

    @Test
    public void testJwtToken() {
        Long userId = 111L;
        String userName = "chenqwwq";
        String host = "127.0.0.1";
        Integer port = 8888;
        PayloadVO vo = new PayloadVO();
        vo.setUserId(userId);
        vo.setUserName(userName);
        vo.setHost(host);
        vo.setPort(port);

        // generate a jwt token
        final String token = JwtUtils.generateToken(userId, vo);
        // verify
        final PayloadVO payloadVO = JwtUtils.verifyToken(token);
        Assert.assertEquals(userId, payloadVO.getUserId());
        Assert.assertEquals(userName, payloadVO.getUserName());
    }
}
