import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
}

val moduleName by extra { "io.opentelemetry.examples.docs.spring-starter" }

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(platform("io.opentelemetry:opentelemetry-bom:1.40.0"))
    implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.6.0"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
    implementation("io.opentelemetry.contrib:opentelemetry-samplers:1.33.0-alpha")
}

springBoot {
    mainClass = "otel.Application"
}
