package otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;

public class InstrumentationLibrary {

  private final Tracer tracer;
  private final Meter meter;

  public InstrumentationLibrary(OpenTelemetry openTelemetry) {
    this.tracer = ProvidersAndScopes.getTracer(openTelemetry);
    this.meter = ProvidersAndScopes.getMeter(openTelemetry);
  }
}
