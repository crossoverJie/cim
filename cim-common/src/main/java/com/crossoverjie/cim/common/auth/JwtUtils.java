package com.crossoverjie.cim.common.auth;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.crossoverjie.cim.common.auth.jwt.dto.PayloadVO;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 签发和验证 JWT
 *
 * @author chenqwwq
 * @date 2025/6/7
 **/
@Slf4j
public class JwtUtils {

    private static final String SECRET = "cim#crossoverjie";

    public static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);
    public static final String issuer = "cim";

    /**
     * Token 签发
     */
    public static <P extends PayloadVO> String generateToken(Long userId, P p) {
        Preconditions.checkNotNull(userId, "userId can't be null");
        Preconditions.checkNotNull(p, "payload can't be null");
        final JSONObject payload = JSONObject.parseObject(JSONObject.toJSONString(p));
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withIssuedAt(new Date())       //  token 签发时间
                .withHeader(createHeader())
                .withPayload(payload)
                .sign(ALGORITHM);
    }

    /**
     * Token 解析
     */
    public static PayloadVO verifyToken(String token) {
        Preconditions.checkArgument(StringUtils.isNotBlank(token), "invalid token");
        final DecodedJWT decodedObj = JWT.require(ALGORITHM)
                .build()
                .verify(token);
        try {
            final PayloadVO vo = new PayloadVO();
            vo.setUserName(decodedObj.getClaim("userName").as(String.class));
            vo.setUserId(decodedObj.getClaim("userId").as(Long.class));
            return vo;
        } catch (Exception e) {
            log.warn("jwt auth token decode failure,token:{},e", token, e);
            throw new JWTDecodeException("user client auth Token decoder failure");
        }
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        return header;
    }
}
