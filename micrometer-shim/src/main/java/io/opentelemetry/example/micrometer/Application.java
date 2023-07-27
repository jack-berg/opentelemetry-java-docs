package io.opentelemetry.example.micrometer;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  // Enable @Timed annotation
  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  // Configure OpenTelemetryMeterRegistry bean, overriding default autoconfigured MeterRegistry bean
  @Bean
  public MeterRegistry meterRegistry() {
    Map<String, String> config = new HashMap<>();
    config.put("otlp.url", "https://otlp.nr-data.net:4318/v1/metrics");
    config.put("otlp.aggregationTemporality", "delta");
    config.put("otlp.headers", "api-key=" + System.getenv("NEW_RELIC_API_KEY"));
    config.put("otlp.resourceAttributes", "service.name=micrometer-otlp");

    return new OtlpMeterRegistry(new OtlpConfig() {
      @Override
      public Duration step() {
        return Duration.ofSeconds(5);
      }

      @Override
      public String get(String key) {
        return config.get(key);
      }
    }, Clock.SYSTEM);
  }
}
