package io.opentelemetry.example.micrometer;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.instrumentation.micrometer.v1_5.OpenTelemetryMeterRegistry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.AggregationTemporalitySelector;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import java.time.Duration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  // Configure OpenTelemetry bean, registering prometheus metric reader
  @Bean
  public OpenTelemetry openTelemetry() {
    return OpenTelemetrySdk.builder()
        .setMeterProvider(
            SdkMeterProvider.builder()
                .setResource(
                    Resource.getDefault().toBuilder()
                        .put("service.name", "My service name")
                        // Include instrumentation.provider=micrometer to enable micrometer metrics
                        // experience in New Relic
                        .put("instrumentation.provider", "micrometer")
                        .build())
                .registerMetricReader(
                    PeriodicMetricReader.builder(
                            OtlpGrpcMetricExporter.builder()
                                .setEndpoint("https://otlp.nr-data.net:4317")
                                .addHeader("api-key", System.getenv("NEW_RELIC_API_KEY"))
                                .setAggregationTemporalitySelector(
                                    AggregationTemporalitySelector.deltaPreferred())
                                .build())
                        // Match default micrometer collection interval of 60 seconds
                        .setInterval(Duration.ofSeconds(10))
                        .build())
                .build())
        .build();
  }

  // Enable @Timed annotation
  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  // Configure OpenTelemetryMeterRegistry bean, overriding default autoconfigured MeterRegistry bean
  @Bean
  public MeterRegistry meterRegistry(OpenTelemetry openTelemetry) {
    return OpenTelemetryMeterRegistry.builder(openTelemetry)
        // Simulate behavior of micrometer's PrometheusMeterRegistry
        .setPrometheusMode(true)
        .build();
  }
}
