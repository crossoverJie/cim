package com.crossoverjie.cim.common.auth;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.crossoverjie.cim.common.auth.jwt.dto.PayloadVO;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 签发和验证 JWT
 *
 * @author chenqwwq
 * @date 2025/6/7
 **/
public class JwtUtils {

    private static final String SECRET = "cim#crossoverjie";

    public static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    public static <P extends PayloadVO> String generateToken(P p) {
        Preconditions.checkNotNull(p, "payload can't be null");
        final JSONObject payload = JSONObject.parseObject(JSONObject.toJSONString(p));
        return JWT.create()
                .withHeader(createHeader())
                .withPayload(payload)
                .sign(ALGORITHM);
    }

    public static DecodedJWT verifyToken(String token) {
        Preconditions.checkArgument(StringUtils.isNotBlank(token), "invalid token");
        final DecodedJWT decode = JWT.decode(token);
        return JWT.require(ALGORITHM)
                .build()
                .verify(decode);
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        return header;
    }
}
