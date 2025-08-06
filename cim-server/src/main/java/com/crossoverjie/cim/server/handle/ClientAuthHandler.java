package com.crossoverjie.cim.server.handle;

import com.crossoverjie.cim.common.auth.JwtUtils;
import com.crossoverjie.cim.common.auth.jwt.dto.PayloadVO;
import com.crossoverjie.cim.common.enums.ChannelAttributeKeys;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.common.util.StringUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

/**
 * client auto Handler
 * <p>
 * This layer of handler is above the business handler, and the data flowing here must need to be authenticated, and after authentication, it will remove itself from the pipeline
 * <p>
 *     decoder.channelRead                                  encoder.write
 *           \                                                  /
 *      auto.channelRead                                 auto.write
 *            \                                             /
 *       cimServerHandle.channelRead      -->>      cimServerHandle.write
 * <p>
 * <p>
 * TODO: 认证是否需要一个超时时间,因为有心跳包,如果超时时间内,心跳包就需要跳过验证
 *
 * @author chenqwwq
 * @date 2025/6/5
 **/
@Slf4j
public class ClientAuthHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final Attribute<Boolean> attr = ctx.channel().attr(ChannelAttributeKeys.AUTH_RES);
        if (BooleanUtils.isTrue(attr.get())) {
            // 已经认证过了就往下传递
            return;
        }

        // 处理 Token
        final String autoToken = ((Request) msg).getReqMsg();
        if (StringUtil.isEmpty(autoToken)) {
            log.error("Token is null");
            writeAndClose("Require authentication first", ctx);
            return;
        }

        try {
            final PayloadVO payload = JwtUtils.verifyToken(autoToken);
            ctx.channel().attr(ChannelAttributeKeys.USER_ID).set(payload.getUserId());
            ctx.channel().attr(ChannelAttributeKeys.USER_NAME).set(payload.getUserName());
            attr.set(Boolean.TRUE);
            // 认证成功之后移除自身
            ctx.pipeline().remove(this);
            ctx.writeAndFlush("auto success ,welcome !");
            return;
        } catch (Exception e) {
            log.error("client auto failure,e:", e);
        }
        writeAndClose("auto failure!", ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        // TODO: 是否需要拦截写的操作
        super.write(ctx, msg, promise);
    }

    private void writeAndClose(String msg, ChannelHandlerContext ctx) {
        // 当前的 Handler 还在就说明链接还未认证
        ctx.writeAndFlush("auto failure!")
                .addListener((ChannelFutureListener) future -> {
                    if (future.isDone()) {
                        log.info("client auth failure,close the channel.");
                        ctx.close();
                    }
                });
    }

}
