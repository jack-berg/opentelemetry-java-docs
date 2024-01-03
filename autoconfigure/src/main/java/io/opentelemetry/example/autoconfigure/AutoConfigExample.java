package io.opentelemetry.example.autoconfigure;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;

import java.util.concurrent.TimeUnit;

/**
 * An example of using {@link io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk} and
 * logging exporter: {@link io.opentelemetry.exporter.logging.LoggingSpanExporter}.
 */
public final class AutoConfigExample {
  private static final String INSTRUMENTATION_NAME = AutoConfigExample.class.getName();

  public static void main(String[] args) throws InterruptedException {
    // Let the SDK configure itself using environment variables and system properties
    OpenTelemetrySdk openTelemetry = AutoConfiguredOpenTelemetrySdk.builder()
            .addSpanExporterCustomizer((spanExporter, configProperties) -> {
              if (spanExporter instanceof OtlpGrpcSpanExporter) {
                spanExporter.shutdown().join(10, TimeUnit.SECONDS);
                ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 4317).userAgent("foo").usePlaintext().build();
                return ((OtlpGrpcSpanExporter) spanExporter).toBuilder().setChannel(managedChannel).build();
              }
              return spanExporter;
            })
            .build().getOpenTelemetrySdk();

    AutoConfigExample example = new AutoConfigExample(openTelemetry);
    // Do some real work that'll emit telemetry
    example.doWork();
  }

  private final Tracer tracer;

  public AutoConfigExample(OpenTelemetry openTelemetry) {
    this.tracer = openTelemetry.getTracer(INSTRUMENTATION_NAME);
  }

  public void doWork() throws InterruptedException {
    Span span =
        tracer
            .spanBuilder("important work")
            .setAttribute("foo", 42)
            .setAttribute("bar", "a string!")
            .startSpan();
    try {
      Thread.sleep(1000);
    } finally {
      span.end();
    }
  }
}
