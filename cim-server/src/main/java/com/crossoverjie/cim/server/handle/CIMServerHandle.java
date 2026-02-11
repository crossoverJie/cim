package com.crossoverjie.cim.server.handle;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import com.crossoverjie.cim.server.config.OtelConfig;
import com.crossoverjie.cim.server.kit.RouteHandler;
import com.crossoverjie.cim.server.kit.ServerHeartBeatHandlerImpl;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 17/05/2018 18:52
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
@Slf4j
public class CIMServerHandle extends SimpleChannelInboundHandler<Request> {

    /**
     * Tracer 用于手动创建 Span（链路追踪的基本单元）
     * 使用懒加载 + volatile 缓存，因为 Spring 容器启动后才能获取 Bean
     */
    private volatile Tracer cachedTracer;

    private Tracer getTracer() {
        if (cachedTracer != null) {
            return cachedTracer;
        }
        try {
            cachedTracer = SpringBeanFactory.getBean(Tracer.class);
            return cachedTracer;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        CIMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
        if (userInfo != null) {
            log.warn("[{}] trigger channelInactive offline!", userInfo.getUserName());

            // Clear route info and offline.
            RouteHandler routeHandler = SpringBeanFactory.getBean(RouteHandler.class);
            routeHandler.userOffLine(userInfo, (NioSocketChannel) ctx.channel());

            ctx.channel().close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {

                log.info("!!READER_IDLE!!");

                HeartBeatHandler heartBeatHandler = SpringBeanFactory.getBean(ServerHeartBeatHandlerImpl.class);
                heartBeatHandler.process(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        log.info("received msg=[{}]", msg.toString());

        if (msg.getCmd() == BaseCommand.LOGIN_REQUEST) {
            // === OpenTelemetry: 创建 login Span ===
            // Span 是一次操作的记录，包含操作名、耗时、属性等
            // 在 Jaeger UI 中可以看到每个 Span 的详细信息
            Tracer tracer = getTracer();
            Span span = tracer != null
                    ? tracer.spanBuilder("cim.server.login").startSpan()
                    : null;
            try {
                // 保存客户端与 Channel 之间的关系
                SessionSocketHolder.put(msg.getRequestId(), (NioSocketChannel) ctx.channel());
                SessionSocketHolder.saveSession(msg.getRequestId(), msg.getReqMsg());
                log.info("client [{}] online success!!", msg.getReqMsg());

                // 在 Span 中记录属性，这些属性会在 Jaeger UI 中显示
                if (span != null) {
                    span.setAttribute("userId", msg.getRequestId());
                    span.setAttribute("userName", msg.getReqMsg());
                    span.setStatus(StatusCode.OK);
                }

                // === Metrics: 递增登录计数器 ===
                if (OtelConfig.getLoginCounter() != null) {
                    OtelConfig.getLoginCounter().increment();
                }
            } catch (Exception e) {
                if (span != null) {
                    span.setStatus(StatusCode.ERROR, e.getMessage());
                    span.recordException(e);
                }
                throw e;
            } finally {
                if (span != null) {
                    span.end(); // 必须调用 end() 来结束 Span，否则不会被导出
                }
            }
        }

        // 心跳更新时间
        if (msg.getCmd() == BaseCommand.PING) {
            NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());

            // === Metrics: 递增心跳计数器 ===
            if (OtelConfig.getHeartbeatCounter() != null) {
                OtelConfig.getHeartbeatCounter().increment();
            }

            // 向客户端响应 pong 消息
            Request heartBeat = SpringBeanFactory.getBean("heartBeat", Request.class);
            ctx.writeAndFlush(heartBeat).addListeners((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("IO error,close Channel");
                    future.channel().close();
                }
            });
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (CIMException.isResetByPeer(cause.getMessage())) {
            return;
        }

        log.error(cause.getMessage(), cause);

    }

}
