package otel;

import static otel.CreateAttributes.CUSTOMER_ID;
import static otel.CreateAttributes.SHOP_NAME;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.metrics.DoubleGauge;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongUpDownCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;

public class Noops {

  private static final String SCOPE_NAME = "fully.qualified.name";

  static void noopTracing() {
    Tracer noopTracer = OpenTelemetry.noop().getTracer(SCOPE_NAME);

    noopTracer
        .spanBuilder("span name")
        .startSpan()
        .setAttribute(SHOP_NAME, "opentelemetry-demo")
        .setStatus(StatusCode.OK)
        .addEvent("event-name", Attributes.builder().put(CUSTOMER_ID, 123).build())
        .end();
  }

  static void noopMetrics() {
    Meter noopMeter = OpenTelemetry.noop().getMeter(SCOPE_NAME);

    Attributes attributes = Attributes.of(SHOP_NAME, "opentelemetry-demo");

    DoubleHistogram histogram = noopMeter.histogramBuilder("com.acme.histogram").build();
    histogram.record(1.0, attributes);

    LongCounter counter = noopMeter.counterBuilder("com.acme.counter").build();
    counter.add(1, attributes);

    LongUpDownCounter upDownCounter =
        noopMeter.upDownCounterBuilder("com.acme.updowncounter").build();
    upDownCounter.add(-1, attributes);

    DoubleGauge gauge = noopMeter.gaugeBuilder("com.acme.gauge").build();
    gauge.set(1.1, attributes);

    noopMeter
        .counterBuilder("com.acme.async-counter")
        .buildWithCallback(observable -> observable.record(10, attributes));

    noopMeter
        .upDownCounterBuilder("com.acme.async-updowncounter")
        .buildWithCallback(observable -> observable.record(10, attributes));

    noopMeter
        .gaugeBuilder("com.acme.async-gauge")
        .buildWithCallback(observable -> observable.record(10, attributes));
  }

  static void noopLogging() {
    Logger noopLogger = OpenTelemetry.noop().getLogsBridge().get(SCOPE_NAME);

    noopLogger
        .logRecordBuilder()
        .setBody("log message")
        .setAttribute(SHOP_NAME, "opentelemetry-demo")
        .setSeverity(Severity.INFO)
        .emit();
  }
}
