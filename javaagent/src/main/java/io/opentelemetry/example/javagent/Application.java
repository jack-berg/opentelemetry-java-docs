package io.opentelemetry.example.javagent;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.internal.GlobUtil;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public OpenTelemetry openTelemetry() {
    OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();

    registerThreadUsageMonitor(openTelemetry);

    return openTelemetry;
  }

  private static void registerThreadUsageMonitor(OpenTelemetry openTelemetry) {
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    Meter meter = openTelemetry.getMeter("meter");
    AttributeKey<String> cpuModeKey = AttributeKey.stringKey("cpu.mode");
    AttributeKey<String> threadNameTemplateKey = AttributeKey.stringKey("thread.name_template");

    meter
        .counterBuilder("jvm.thread.time")
        .setUnit("s")
        .ofDoubles()
        .buildWithCallback(
            observable -> {
              Map<String, Long> userCpuTimeByThreadTemplate = new HashMap<>();
              Map<String, Long> systemCputTimeByThreadTemplate = new HashMap<>();

              for (ThreadInfo threadInfo : threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 0)) {
                String threadNameTemplate = threadNameTemplate(threadInfo.getThreadName());
                long threadId = threadInfo.getThreadId();
                long totalCpuTime = threadMXBean.getThreadCpuTime(threadId);
                long userCpuTime = threadMXBean.getThreadUserTime(threadId);
                if (totalCpuTime > -1 && userCpuTime > -1) {
                  userCpuTimeByThreadTemplate.compute(
                      threadNameTemplate,
                      (attributes, value) -> value == null ? userCpuTime : userCpuTime + value);

                  long systemCpuTime = totalCpuTime - userCpuTime;
                  systemCputTimeByThreadTemplate.compute(
                      threadNameTemplate,
                      (attributes, value) -> value == null ? systemCpuTime : systemCpuTime + value);
                }
              }

              userCpuTimeByThreadTemplate.forEach(
                  (threadNameTemplate, value) ->
                      observable.record(
                          toSeconds(value),
                          Attributes.of(
                              cpuModeKey, "user", threadNameTemplateKey, threadNameTemplate)));
              systemCputTimeByThreadTemplate.forEach(
                  (threadNameTemplate, value) ->
                      observable.record(
                          toSeconds(value),
                          Attributes.of(
                              cpuModeKey, "system", threadNameTemplateKey, threadNameTemplate)));
            });
  }

  private static double toSeconds(long ns) {
    return ns / 1.0e9;
  }

  // Collection of well known thread name patterns to match against
  private static final List<GlobPatternAndPredicate> patternAndPredicates =
      Stream.of(
              "http-nio-*-exec-*",
              "http-nio-*-*",
              "BatchSpanProcessor_WorkerThread-*",
              "BatchLogRecordProcessor_WorkerThread-*",
              "okhttp-dispatch-*",
              "Catalina-utility-*")
          .map(GlobPatternAndPredicate::new)
          .toList();

  private static String threadNameTemplate(String threadName) {
    for (GlobPatternAndPredicate patternAndPredicate : patternAndPredicates) {
      if (patternAndPredicate.predicate.test(threadName)) {
        return patternAndPredicate.globPattern;
      }
    }
    logger.info("Unmatched thread name: " + threadName);
    return "other";
  }

  private static class GlobPatternAndPredicate {
    private final String globPattern;
    private final Predicate<String> predicate;

    private GlobPatternAndPredicate(String globPattern) {
      this.globPattern = globPattern;
      this.predicate = GlobUtil.toGlobPatternPredicate(globPattern);
    }
  }
}
