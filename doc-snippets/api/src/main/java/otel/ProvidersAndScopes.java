package otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.logs.LoggerProvider;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.MeterProvider;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.TracerProvider;

public class ProvidersAndScopes {

  private static final String SCOPE_NAME = "fully.qualified.name";
  private static final String SCOPE_VERSION = "0.1";

  static Tracer getTracer(OpenTelemetry openTelemetry) {
    TracerProvider tracerProvider = openTelemetry.getTracerProvider();
    return tracerProvider
        .tracerBuilder(SCOPE_NAME)
        .setInstrumentationVersion(SCOPE_VERSION)
        .build();
  }

  static Meter getMeter(OpenTelemetry openTelemetry) {
    MeterProvider meterProvider = openTelemetry.getMeterProvider();
    return meterProvider.meterBuilder(SCOPE_NAME).setInstrumentationVersion(SCOPE_VERSION).build();
  }

  static Logger getLogger(OpenTelemetry openTelemetry) {
    LoggerProvider loggerProvider = openTelemetry.getLogsBridge();
    return loggerProvider
        .loggerBuilder(SCOPE_NAME)
        .setInstrumentationVersion(SCOPE_VERSION)
        .build();
  }
}
