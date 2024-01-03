plugins {
    id("java")
}

description = "OpenTelemetry Examples for SDK autoconfiguration"
val moduleName by extra { "io.opentelemetry.examples.autoconfigure" }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-exporter-logging")
    implementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp") {
        exclude("io.opentelemetry", "opentelemetry-exporter-sender-okhttp")
    }
    implementation("io.opentelemetry:opentelemetry-exporter-sender-grpc-managed-channel")
    implementation(platform("io.grpc:grpc-bom:1.60.1"))
    implementation("io.grpc:grpc-netty")
}
