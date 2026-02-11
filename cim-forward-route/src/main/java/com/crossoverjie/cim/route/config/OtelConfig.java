package com.crossoverjie.cim.route.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry 配置类 - cim-forward-route
 *
 * 负责初始化 OpenTelemetry SDK，提供 Tracer 用于手动创建 Span，
 * 并注册自定义 Metrics（群聊/私聊路由数、登录/注册数等）。
 *
 * 与 cim-server 的 OtelConfig 不同之处：
 * - service.name 为 "cim-forward-route"
 * - 注册的 Metrics 是路由层特有的指标
 * - Route 的 Controller 由 Spring 管理，可以直接注入 Counter Bean
 */
@Slf4j
@Configuration
public class OtelConfig {

    @Value("${management.otlp.tracing.endpoint:http://localhost:4318/v1/traces}")
    private String otlpEndpoint;

    private SdkTracerProvider tracerProvider;

    /**
     * 创建 OpenTelemetry SDK 实例
     * 与 cim-server 的配置类似，但 service.name 为 "cim-forward-route"
     */
    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
                .merge(Resource.create(Attributes.of(
                        AttributeKey.stringKey("service.name"), "cim-forward-route")));

        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpoint.replace("/v1/traces", "").replace("4318", "4317"))
                .build();

        tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .build();

        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();

        log.info("OpenTelemetry SDK initialized for cim-forward-route, OTLP endpoint={}", otlpEndpoint);
        return openTelemetry;
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("cim-forward-route", "1.0.0");
    }

    // ========== 自定义 Metrics ==========
    // Route 的 Counter Bean 可以直接注入到 Controller 中使用

    /**
     * 群聊消息路由计数器
     */
    @Bean
    public Counter groupMessageCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cim.route.messages.group")
                .description("Total number of group messages routed")
                .register(meterRegistry);
    }

    /**
     * 私聊消息路由计数器
     */
    @Bean
    public Counter p2pMessageCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cim.route.messages.p2p")
                .description("Total number of P2P messages routed")
                .register(meterRegistry);
    }

    /**
     * 登录请求计数器
     */
    @Bean
    public Counter loginCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cim.route.login.count")
                .description("Total number of login requests processed")
                .register(meterRegistry);
    }

    /**
     * 注册请求计数器
     */
    @Bean
    public Counter registerCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cim.route.register.count")
                .description("Total number of register requests processed")
                .register(meterRegistry);
    }

    @PreDestroy
    public void shutdown() {
        if (tracerProvider != null) {
            tracerProvider.shutdown();
            log.info("OpenTelemetry TracerProvider shut down");
        }
    }
}
