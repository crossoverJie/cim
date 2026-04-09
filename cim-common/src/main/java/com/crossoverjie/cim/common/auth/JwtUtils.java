package com.crossoverjie.cim.common.auth;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.crossoverjie.cim.common.auth.jwt.dto.PayloadVO;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 签发和验证 JWT Token
 *
 * @author chenqwwq
 * @date 2025/6/7
 **/
@Slf4j
public class JwtUtils {

    private static final String SECRET = "cim#crossoverjie";

    public static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);
    public static final String ISSUER = "cim";

    /**
     * Token 签发
     */
    public static <P extends PayloadVO> String generateToken(Long userId, P p) {
        Preconditions.checkNotNull(userId, "userId can't be null");
        Preconditions.checkNotNull(p, "payload can't be null");
        final JSONObject payload = JSONObject.parseObject(JSONObject.toJSONString(p));
        final LocalDateTime now = LocalDateTime.now();
        // 签发时间
        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        // 过期时间
        Date expiresAt = Date.from(now.plusMinutes(1)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(String.valueOf(userId))
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withHeader(createHeader())
                .withClaim("payload", payload)
                .sign(ALGORITHM);
    }

    /**
     * Token 解析
     *
     * @param token JWT token
     * @return PayloadVO 解析后的 payload
     * @throws TokenExpiredException token 过期时抛出
     * @throws JWTDecodeException    token 解码失败时抛出
     */
    public static PayloadVO verifyToken(String token) {
        Preconditions.checkArgument(StringUtils.isNotBlank(token), "invalid token");
        try {
            final DecodedJWT decodedObj = JWT.require(ALGORITHM)
                    .build()
                    .verify(token);
            return decodedObj.getClaim("payload").as(PayloadVO.class);
        } catch (TokenExpiredException e) {
            log.warn("jwt token has expired, token:{}", token);
            throw e;
        } catch (JWTDecodeException e) {
            log.warn("jwt auth token decode failure, token:{}", token, e);
            throw e;
        } catch (Exception e) {
            log.warn("jwt auth token verify failure, token:{}", token, e);
            throw new JWTDecodeException("user client auth Token decoder failure");
        }
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        return header;
    }
}
