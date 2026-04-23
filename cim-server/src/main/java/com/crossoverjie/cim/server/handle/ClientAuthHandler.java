package com.crossoverjie.cim.server.handle;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.auth.JwtUtils;
import com.crossoverjie.cim.common.auth.jwt.dto.PayloadVO;
import com.crossoverjie.cim.common.enums.ChannelAttributeKeys;
import com.crossoverjie.cim.common.msg.ChannelAuthReq;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.common.protocol.Response;
import com.crossoverjie.cim.common.util.StringUtil;
import com.crossoverjie.cim.server.config.ServerConfig;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * client auto Handler
 * <p>
 * This layer of handler is above the business handler, and the data flowing here must need to be authenticated,
 * and after authentication, it will remove itself from the pipeline
 * <p>
 * decoder.channelRead                                  encoder.write
 * \                                                  /
 * auto.channelRead                                 auto.write
 * \                                             /
 * cimServerHandle.channelRead      -->>      cimServerHandle.write
 * <p>
 * <p>
 * TODO: 认证是否需要一个超时时间,因为有心跳包,如果超时时间内,心跳包就需要跳过验证
 *
 * @author chenqwwq
 * @date 2025/6/5
 **/
@Slf4j
public class ClientAuthHandler extends ChannelDuplexHandler {


    private ServerConfig serverConfig;

    public ClientAuthHandler(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Attribute<Boolean> attr = ctx.channel().attr(ChannelAttributeKeys.AUTH_RES);
        if (BooleanUtils.isTrue(attr.get())) {
            // 已经认证过了就往下传递,可能存在认证通过但没有移除 Handler 的情况
            super.channelRead(ctx, msg);
            return;
        }

        if (Objects.isNull(msg)) {
            log.warn("Received null message during authentication");
            return;
        }

        if (!(msg instanceof Request)) {
            log.warn("Received non-Request message during authentication: {}", msg.getClass());
            return;
        }

        // 处理 Token
        final String body = ((Request) msg).getReqMsg();
        if (StringUtil.isEmpty(body)) {
            log.error("auth body is null");
            writeAndClose("Require authentication first", ctx);
            return;
        }

        try {
            final ChannelAuthReq req = JSONObject.parseObject(body, ChannelAuthReq.class);
            if (Objects.isNull(req) || StringUtils.isBlank(req.getAuthToken())) {
                log.error("auth req is illegal");
                writeAndClose("auth req is illegal", ctx);
                return;
            }
            final PayloadVO payload = JwtUtils.verifyToken(req.getAuthToken());
            // 校验 host / port 是否匹配当前服务
            if (!isHostMatch(payload.getHost()) || !isPortMatch(payload.getPort())) {
                log.error("host or port mismatch, payload host={}, port={}, server host={}, port={}",
                        payload.getHost(), payload.getPort(),
                        serverConfig.getHost(), serverConfig.getNettyPort());
                writeAndClose("host or port not allowed", ctx);
                return;
            }
            ctx.channel().attr(ChannelAttributeKeys.USER_ID).set(payload.getUserId());
            ctx.channel().attr(ChannelAttributeKeys.USER_NAME).set(payload.getUserName());
            attr.set(Boolean.TRUE);
            // 认证成功之后移除自身
            ctx.pipeline().remove(this);
            // 发送欢迎信息
            final Response welcome = Response.newBuilder()
                    .setResponseId(-1)
                    .setResMsg("auth success,welcome to cim,You can speak freely")
                    .setCmd(BaseCommand.PING)
                    .build();
            ctx.writeAndFlush(welcome);
            ctx.fireChannelRead(msg);       // 继续向后传递
        } catch (Exception e) {
            log.error("client auth failure,e:", e);
            writeAndClose("auth failure!", ctx);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // TODO: 是否需要拦截写的操作
        super.write(ctx, msg, promise);
    }

    private void writeAndClose(String msg, ChannelHandlerContext ctx) {
        // 当前的 Handler 还在就说明链接还未认证
        final Response resp = Response.newBuilder()
                .setResponseId(-1)
                .setResMsg(msg)
                .setCmd(BaseCommand.MESSAGE)
                .build();
        ctx.writeAndFlush(resp)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isDone()) {  // 结束就行,不关是否成功
                        log.info("client auth failure,close the channel.");
                    }
                    ctx.close();
                });
    }

    /**
     * 校验 host 是否匹配当前服务，忽略回环地址
     */
    private boolean isHostMatch(String payloadHost) {
        if (StringUtils.isBlank(payloadHost)) {
            return false;
        }
        // 回环地址直接放行
        if ("127.0.0.1".equals(payloadHost) || "0.0.0.0".equals(payloadHost) || "localhost".equals(payloadHost)) {
            return true;
        }
        return payloadHost.equals(serverConfig.getHost());
    }

    /**
     * 校验 port 是否匹配当前服务
     */
    private boolean isPortMatch(Integer payloadPort) {
        if (payloadPort == null) {
            return false;
        }
        return payloadPort.equals(serverConfig.getNettyPort());
    }

}
