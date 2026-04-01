package com.crossoverjie.cim.common.kit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Function: 测试心跳处理类
 * Date: 01/04/2026 20:00
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class HeartBeatHandlerTest {

    @Mock
    private ChannelHandlerContext mockCtx;

    private EmbeddedChannel channel;
    private TestHeartBeatHandler testHandler;

    /**
     * 初始化测试环境
     */
    @BeforeEach
    public void setUp() {
        channel = new EmbeddedChannel();
        testHandler = new TestHeartBeatHandler();
    }

    /**
     * 测试处理正常情况
     */
    @Test
    public void testProcessSuccess() throws Exception {
        testHandler.process(mockCtx);
        verify(mockCtx, times(1)).channel();
    }

    /**
     * 测试处理异常情况
     */
    @Test
    public void testProcessWithException() throws Exception {
        doThrow(new RuntimeException("Test exception")).when(mockCtx).channel();

        assertThrows(RuntimeException.class, () -> {
            testHandler.process(mockCtx);
        });
    }

    /**
     * 测试处理 ChannelInactive 情况
     */
    @Test
    public void testProcessChannelInactive() throws Exception {
        when(mockCtx.channel()).thenReturn(null);
        testHandler.process(mockCtx);
        verify(mockCtx, times(1)).channel();
    }

    /**
     * 测试处理多个调用情况
     */
    @Test
    public void testProcessMultipleCalls() throws Exception {
        for (int i = 0; i < 5; i++) {
            testHandler.process(mockCtx);
        }
        verify(mockCtx, times(5)).channel();
    }

    /**
     * 测试用的 HeartBeatHandler 实现类
     */
    private static class TestHeartBeatHandler implements HeartBeatHandler {
        @Override
        public void process(ChannelHandlerContext ctx) throws Exception {
            if (ctx != null && ctx.channel() != null) {
                ctx.channel().isActive();
            }
        }
    }
}
