package com.crossoverjie.cim.server.handle;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.crossoverjie.cim.common.auth.JwtUtils;
import com.crossoverjie.cim.common.auth.jwt.dto.PayloadVO;
import com.crossoverjie.cim.common.enums.ChannelAttributeKeys;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.common.util.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

/**
 * 客户端认证 Handler
 * <p>
 * 流到这里的数据肯定是需要认证的,认证完成后会从 Pipeline 中将自身移除
 *
 * @author chenqwwq
 * @date 2025/6/5
 **/
@Slf4j
public class ClientAuthHandler extends SimpleChannelInboundHandler<Request> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        final Attribute<Boolean> attr = ctx.channel().attr(ChannelAttributeKeys.AUTH_RES);
        if (BooleanUtils.isTrue(attr.get())) {
            // 已经认证过了就往下传递
            ctx.fireChannelRead(msg);
            return;
        }
        // 处理 Token
        final String autoToken = msg.getReqMsg();
        if (StringUtil.isEmpty(autoToken)) {
            log.error("Token is null");
            ctx.writeAndFlush("Require authentication first");
            ctx.close();
            return;
        }

        try {
            final DecodedJWT decodedJWT = JwtUtils.verifyToken(autoToken);
            final String raw = decodedJWT.getPayload();
            final PayloadVO payload = JSONObject.parseObject(raw, PayloadVO.class);
            attr.set(Boolean.TRUE);
            ctx.channel().attr(ChannelAttributeKeys.USER_ID).set(payload.getUserId());
            ctx.channel().attr(ChannelAttributeKeys.USER_NAME).set(payload.getUserName());
            // 认证成功之后移除自身
            ctx.pipeline().remove(this);
            ctx.write("auto success ,welcome !");
        } catch (Exception e) {
            log.error("client auto failure,e:", e);
        }
        ctx.write("auto failure!");
        ctx.close();
    }
}
