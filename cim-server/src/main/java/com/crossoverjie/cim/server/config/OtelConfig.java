package com.crossoverjie.cim.server.config;

import com.crossoverjie.cim.server.util.SessionSocketHolder;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributeKey;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry 配置类 - cim-server
 *
 * 负责初始化 OpenTelemetry SDK，提供 Tracer 用于手动创建 Span，
 * 并注册自定义 Metrics（在线用户数、消息推送数等）。
 *
 * 工作原理：
 * 1. Tracer 用于在 Netty Handler 中手动创建 Span（因为 Netty TCP 不会被 Spring 自动埋点覆盖）
 * 2. Metrics 通过 Micrometer 的 MeterRegistry 注册，由 micrometer-registry-otlp 自动导出
 * 3. Span 通过 OTLP Exporter 发送到 Jaeger
 */
@Slf4j
@Configuration
public class OtelConfig {

    @Value("${management.otlp.tracing.endpoint:http://localhost:4318/v1/traces}")
    private String otlpEndpoint;

    private SdkTracerProvider tracerProvider;

    /**
     * 自定义 Metrics 计数器，使用 static 以便在 Netty Handler 中访问
     * （Netty Handler 不由 Spring 管理，无法注入 Bean）
     */
    @Getter
    private static Counter loginCounter;
    @Getter
    private static Counter heartbeatCounter;
    @Getter
    private static Counter messagePushedCounter;

    /**
     * 创建 OpenTelemetry SDK 实例
     *
     * Resource: 标识当前服务名称（在 Jaeger UI 中显示）
     * OtlpGrpcSpanExporter: 将 Span 通过 gRPC 发送到 Jaeger
     * BatchSpanProcessor: 批量发送 Span 以提高性能
     */
    @Bean
    public OpenTelemetry openTelemetry() {
        // Resource 用于标识当前服务，在 Jaeger UI 的 Service 下拉框中会显示这个名称
        Resource resource = Resource.getDefault()
                .merge(Resource.create(Attributes.of(
                        AttributeKey.stringKey("service.name"), "cim-server")));

        // OTLP Exporter：将 Trace 数据发送到 Jaeger 的 OTLP 端点
        // Jaeger 默认在 4317 端口接收 gRPC 格式的 OTLP 数据
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpoint.replace("/v1/traces", "").replace("4318", "4317"))
                .build();

        // TracerProvider：管理 Tracer 的创建和生命周期
        // BatchSpanProcessor 会将 Span 缓冲后批量发送，减少网络开销
        tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .build();

        OpenTelemetrySdk openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();

        log.info("OpenTelemetry SDK initialized for cim-server, OTLP endpoint={}", otlpEndpoint);
        return openTelemetry;
    }

    /**
     * 创建 Tracer Bean
     *
     * Tracer 用于在代码中手动创建 Span，例如：
     * Span span = tracer.spanBuilder("cim.server.login").startSpan();
     * try (Scope scope = span.makeCurrent()) {
     * // 业务逻辑
     * span.setAttribute("userId", userId);
     * } finally {
     * span.end();
     * }
     */
    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("cim-server", "1.0.0");
    }

    /**
     * 注册自定义 Metrics
     *
     * Gauge: 瞬时值指标，如当前在线用户数
     * Counter: 累计值指标，如消息推送总数
     *
     * 这些指标可以通过以下方式查看：
     * 1. Actuator 端点：http://localhost:8081/actuator/metrics/cim.server.online.users
     * 2. OTLP 导出到后端（如 Prometheus）
     */
    @Bean
    public String registerMetrics(MeterRegistry meterRegistry) {
        // Gauge: 在线用户数 - 每次采集时从 SessionSocketHolder 获取当前值
        Gauge.builder("cim.server.online.users",
                () -> SessionSocketHolder.getRelationShip().size())
                .description("Current number of online users connected to this server")
                .register(meterRegistry);

        // Counter: 登录请求总数
        loginCounter = Counter.builder("cim.server.login.count")
                .description("Total number of login requests processed")
                .register(meterRegistry);

        // Counter: 心跳消息总数
        heartbeatCounter = Counter.builder("cim.server.heartbeat.count")
                .description("Total number of heartbeat (PING) messages received")
                .register(meterRegistry);

        // Counter: 推送消息总数
        messagePushedCounter = Counter.builder("cim.server.messages.pushed")
                .description("Total number of messages pushed to clients")
                .register(meterRegistry);

        log.info("CIM server custom metrics registered");
        return "metricsRegistered";
    }

    @PreDestroy
    public void shutdown() {
        if (tracerProvider != null) {
            tracerProvider.shutdown();
            log.info("OpenTelemetry TracerProvider shut down");
        }
    }
}
