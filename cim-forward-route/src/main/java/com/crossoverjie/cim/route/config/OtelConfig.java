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
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry 配置类 - cim-forward-route
 *
 * 负责初始化 OpenTelemetry SDK，提供 Tracer 用于手动创建 Span，
 * 并注册自定义 Metrics（群聊/私聊路由数、登录/注册数等）。
 */
@Slf4j
@Configuration
public class OtelConfig {

    @Value("${otel.exporter.otlp.grpc.endpoint:http://localhost:4317}")
    private String otlpGrpcEndpoint;

    private SdkTracerProvider tracerProvider;

    @jakarta.annotation.Resource
    private MeterRegistry meterRegistry;

    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
                .merge(Resource.create(Attributes.of(
                        AttributeKey.stringKey("service.name"), "cim-forward-route")));

        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpGrpcEndpoint)
                .build();

        tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .build();

        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();

        log.info("OpenTelemetry SDK initialized for cim-forward-route, OTLP gRPC endpoint={}", otlpGrpcEndpoint);
        return openTelemetry;
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("cim-forward-route", "1.0.0");
    }

    // ========== 自定义 Metrics ==========

    @Bean
    public Counter groupMessageCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cim.route.messages.group")
                .description("Total number of group messages routed")
                .register(meterRegistry);
    }

    @Bean
    public Counter p2pMessageCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cim.route.messages.p2p")
                .description("Total number of P2P messages routed")
                .register(meterRegistry);
    }

    @Bean
    public Counter loginCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cim.route.login.count")
                .description("Total number of login requests processed")
                .register(meterRegistry);
    }

    @Bean
    public Counter registerCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cim.route.register.count")
                .description("Total number of register requests processed")
                .register(meterRegistry);
    }

    @PreDestroy
    public void shutdown() {
        if (tracerProvider != null) {
            tracerProvider.shutdown().join(10, TimeUnit.SECONDS);
            log.info("OpenTelemetry TracerProvider shut down");
        }
    }
}
