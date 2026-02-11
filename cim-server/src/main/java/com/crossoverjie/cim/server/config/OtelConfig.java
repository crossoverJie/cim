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
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
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
 */
@Slf4j
@Configuration
public class OtelConfig {

        @Value("${otel.exporter.otlp.grpc.endpoint:http://localhost:4317}")
        private String otlpGrpcEndpoint;

        private SdkTracerProvider tracerProvider;

        @jakarta.annotation.Resource
        private MeterRegistry meterRegistry;

        /**
         * 自定义 Metrics 计数器，使用 static volatile 以便在 Netty Handler 中线程安全访问
         */
        @Getter
        private static volatile Counter loginCounter;
        @Getter
        private static volatile Counter heartbeatCounter;
        @Getter
        private static volatile Counter messagePushedCounter;

        @Bean
        public OpenTelemetry openTelemetry() {
                Resource resource = Resource.getDefault()
                                .merge(Resource.create(Attributes.of(
                                                AttributeKey.stringKey("service.name"), "cim-server")));

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

                log.info("OpenTelemetry SDK initialized for cim-server, OTLP gRPC endpoint={}", otlpGrpcEndpoint);
                return openTelemetry;
        }

        @Bean
        public Tracer tracer(OpenTelemetry openTelemetry) {
                return openTelemetry.getTracer("cim-server", "1.0.0");
        }

        @PostConstruct
        public void registerMetrics() {
                Gauge.builder("cim.server.online.users",
                                () -> SessionSocketHolder.getRelationShip().size())
                                .description("Current number of online users connected to this server")
                                .register(meterRegistry);

                loginCounter = Counter.builder("cim.server.login.count")
                                .description("Total number of login requests processed")
                                .register(meterRegistry);

                heartbeatCounter = Counter.builder("cim.server.heartbeat.count")
                                .description("Total number of heartbeat (PING) messages received")
                                .register(meterRegistry);

                messagePushedCounter = Counter.builder("cim.server.messages.pushed")
                                .description("Total number of messages pushed to clients")
                                .register(meterRegistry);

                log.info("CIM server custom metrics registered");
        }

        @PreDestroy
        public void shutdown() {
                if (tracerProvider != null) {
                        tracerProvider.shutdown().join(10, TimeUnit.SECONDS);
                        log.info("OpenTelemetry TracerProvider shut down");
                }
        }
}
