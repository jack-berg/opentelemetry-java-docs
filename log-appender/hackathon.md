
# Summary

The java logging ecosystem is fractured, with a variety of competing libraries for recording logs and configuring how they are processed.

Terminology:

* **Log API**: A library meant to be called by application code and libraries to record / emit log records.
* **Log Bridge**: A library that converts from one logging API / SDK to another.
* **Log SDK**: A library that processes logs. Common tasks include filtering, formatting / templating, export (file, stream, network location, etc). 

TODO: add entries for JCL (jakarta commons logging)

https://drive.google.com/file/d/1xYPEDfTcdKnMiDviKtnIBSwe0NVQNTYD/view?usp=drive_link

## Log APIs

These are APIs meant to be used by libraries and applications to record logs. I.e. `logger.info("hello world")`.

| Name               | Maven coordinates                    | Description                                           | Usage                                                                                                                |
|--------------------|--------------------------------------|-------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| OpenTelemetry API  | `io.opentelemetry:opentelemetry-api` | OpenTelemetry log APIs, along with metrics and traces | `opentelemetry.getLogsBridge().get("my-logger").logRecordBuilder().setSeverity(INFO).setBody("Hello world").emit();` |
| SLF4J API          | `org.slf4j:slf4j-api`                | Standard logging facade for java API                  | `LoggerFactory.getLogger("my-logger").info("Hello world");`                                                          |
| JUL API            | built into java since 1.4            | JUL - Java utility logger                             | `Logger.getLogger("my-logger").info("Hello world");`                                                                 |
| Log4j2 API         | `org.apache.logging.log4j:log4j-api` | Log4j2 API                                            | `LogManager.getLogger("my-logger").info("Hello world");`                                                             |
| Log4j1 API / SDK   | `log4j:log4j`                        | Log4j1 API / SDK (EOL)                                | `Logger.getLogger("my-logger").info("Hello world");`                                                                 |
| Reload4j API / SDK | `ch.qos.reload4j:reload4j:1.2.26`    | Reload4j API / SDK - drop in replacement for Log4j1   | `Logger.getLogger("my-logger").info("Hello world");` (identical API as Log4j1)                                       |

## Log Bridges

These bridge logs from one toolkit to another. They work in a variety of ways depending on the source and target.

| Source      | Target                    | Maven coordinates                                                                                             | Description                                                                                                                                         | Usage                                                                                                                                                 |
|-------------|---------------------------|---------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| Log4j2 SDK  | OpenTelemetry API         | `io.opentelemetry.instrumentation:opentelemetry-log4j-appender-2.17`                                          | Bridge logs from Log4j2 SDK to OpenTelemetry log API by implementing `Appender` interface referenced in Log4j2 SDK config                           | Include `<AppenderRef ref="OpenTelemetryAppender" />` in `log4j2.xml`, and call `OpenTelemetryAppender.install(OpenTelemetry)`                        |
| Logback SDK | OpenTelemetry API         | `io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0`                                         | Bridge logs from Logback SDK to OpenTelemetry log API by implementing `Appender` interface referenced in Logback SDK config                         | Include `<appender name="OpenTelemetry" class="*.OpenTelemetryAppender" />` in `logback.xml`, and call `OpenTelemetryAppender.install(OpenTelemetry)` |
| Log4j1 API  | OpenTelemetry API         | `io.opentelemetry.javaagent:opentelemetry-javaagent`                                                          | Bridge logs from Log4j1 API to OpenTelemetry log API using bytecode manipulation                                                                    | Include `-javaagent=/path/to/opentelemetry-javaagent.jar`                                                                                             |
| Log4j2 API  | OpenTelemetry API         | `io.opentelemetry.javaagent:opentelemetry-javaagent`                                                          | Bridge logs from Log4j2 API to OpenTelemetry log API using bytecode manipulation                                                                    | Include `-javaagent=/path/to/opentelemetry-javaagent.jar`                                                                                             |
| Logback API | OpenTelemetry API         | `io.opentelemetry.javaagent:opentelemetry-javaagent`                                                          | Bridge logs from Logback API to OpenTelemetry log API using bytecode manipulation                                                                   | Include `-javaagent=/path/to/opentelemetry-javaagent.jar`                                                                                             |
| JUL API     | OpenTelemetry API         | `io.opentelemetry.javaagent:opentelemetry-javaagent`                                                          | Bridge logs from JUL API to OpenTelemetry log API using bytecode manipulation                                                                       | Include `-javaagent=/path/to/opentelemetry-javaagent.jar`                                                                                             |
| JUL API     | OpenTelemetry API         | `io.opentelemetry.javaagent:opentelemetry-javaagent`                                                          | Bridge logs from JUL API to OpenTelemetry log API using bytecode manipulation                                                                       | Include `-javaagent=/path/to/opentelemetry-javaagent.jar`                                                                                             |
| JUL API     | SLF4J API                 | `org.slf4j:jul-to-slf4j`                                                                                      | Bridge logs from JUL to SLF4J by adding `Handler` to JUL root logger                                                                                | Call `SLF4JBridgeHandler.removeHandlersForRootLogger()`, `SLF4JBridgeHandler.install()`                                                               |
| Log4j1 API  | SLF4J API                 | `org.slf4j:log4j-over-slf4j`                                                                                  | Bridge logs from Log4j1 SDK to SLF4J by reimplementing Log4j1 API classes                                                                           | Replace `log4j.jar` with `log4j-over-slf4j.jar` on classpath                                                                                          |
| Log4j2 API  | SLF4J API                 | `org.apache.logging.log4j:log4j-to-slf4j`                                                                     | Bridge logs from Log4j2 SDK to SLF4J by implementing the Log42J API `Provider` interface used to bind the Log4j2 API to an implementation           | Include `log4j-to-slf4j.jar` on classpath                                                                                                             |
| SLF4J API   | JUL SDK                   | `org.slf4j:slf4j-jdk14`                                                                                       | Bridge logs from SLF4J to JUL by implementing SLF4J API `SLF4JServiceProvider` interface used to bind SLF4J API to an implementation                | Include `slf4j-jdk14.jar` on classpath                                                                                                                |
| SLF4J API   | Reload4j SDK / Log4j1 SDK | `org.slf4j:slf4j-reload4j`                                                                                    | Bridge logs from SLF4J to Reload4j or Log4j1 by implementing SLF4J API `SLF4JServiceProvider` interface used to bind SLF4J API to an implementation | Include `slf4j-reload4j.jar` on classpath                                                                                                             |
| SLF4J API   | Logback SDK               | `ch.qos.logback:logback-classic`                                                                              | Bridge logs from SLF4J to Logback by implementing SLF4J API `SLF4JServiceProvider` interface used to bind SLF4J API to an implementation            | Include `logback-classic.jar` on classpath                                                                                                            |
| SLF4J API   | Log4j2 SDK                | `org.apache.logging.log4j:log4j-slf4j2-impl` (`org.apache.logging.log4j:log4j-slf4j-impl` for older versions) | Bridge logs from SLF4J to Log4j2 by implementing SLF4J API `SLF4JServiceProvider` interface used to bind SLF4J API to an implementation             | Include `log4j-slf4j2-impl.jar` on classpath                                                                                                          |
| 


## Log SDKs

| Name               | Maven coordinates                     | Description                                          |
|--------------------|---------------------------------------|------------------------------------------------------|
| OpenTelemetry SDK  | `io.opentelemetry:opentelemetry-sdk`  | OpenTelemetry log SDK, along with metrics and traces |
| Logback SDK        | `ch.qos.logback:logback-core`         | Logback SDK                                          |
| Log4j2 SDK         | `org.apache.logging.log4j:log4j-core` | Log4j2 SDK                                           |
| Log4j1 API / SDK   | `log4j:log4j`                         | Log4j1 API / SDK (EOL)                               |
| Reload4j API / SDK | `ch.qos.reload4j:reload4j:1.2.26`     | Reload4j API / SDK - drop in replacement for Log4j1  |
| JUL SDK            | built into java since 1.4             | JUL SDK                                              |              

## Matrix

|                       | OpenTelemetry API         | SLF4J API                                                                                                                                        | JUL API                                                                                          | Log4j2 API                                                   | Log4j1 / Reload4j API                                                                                |
|-----------------------|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|--------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| OpenTelemetry SDK     | yes (`opentelemetry-sdk`) | yes if log4j2 / logback SDK (`logback-classic`, `opentelemetry-logback-appender-1.0` OR `log4j-slf4j-impl`, `opentelemetry-log4j-appender-2.17`) | yes if log4j2 / logback SDK (`jul-to-slf4j` + SLF4J API -> OpenTelemetry SDK), or otel javaagent | yes (`opentelemetry-log4j-appender-2.17`), or otel javaagent | yes if log4j2 / logback SDK (`log4j-over-slf4j` + SLF4J API -> OpenTelemetry SDK), or otel javaagent |
| Logback SDK           | no                        | yes (`logback-classic`)                                                                                                                          | yes (`jul-to-slf4j`, `logback-classic`)                                                          | yes (`log4j-to-slf4j`, `logback-classic`)                    | yes (`log4j-over-slf4j`, `logback-classic`)                                                          |
| JUL SDK               | no                        | yes (`slf4j-jdk14`)                                                                                                                              | yes (built in)                                                                                   | yes (`log4j-to-slf4j`, `slf4j-jdk14`)                        | yes (`log4j-over-slf4j`, `slf4j-jdk14`)                                                              |
| Log4j2 SDK            | no                        | yes (`log4j-slf4j2-impl`)                                                                                                                        | yes (`jul-to-slf4j`, `log4j-slf4j2-impl`)                                                        | yes (`log4j-core`)                                           | yes (`log4j-over-slf4j`, `log4j-slf4j2-impl`)                                                        |
| Log4j1 / Reload4j SDK | no                        | yes (`slf4j-reload4j`)                                                                                                                           | yes (`jul-to-slf4j`, `slf4j-reload4j`)                                                           | yes (`log4j-to-slf4j`, `slf4j-reload4j`)                     | yes (built in)                                                                                       |

## Key Links

* [SLF4J docs](https://www.slf4j.org/manual.html)
  * [Bridging legacy APIs](https://www.slf4j.org/legacy.html)
* [Log4j2 docs](https://logging.apache.org/log4j/2.12.x/manual/)
* [reload4j docs](https://reload4j.qos.ch/)
* [Logback docs](https://logback.qos.ch/manual/index.html)
* [JUL docs](https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html)
* [Log4j1 docs](https://logging.apache.org/log4j/1.x/manual.html)
