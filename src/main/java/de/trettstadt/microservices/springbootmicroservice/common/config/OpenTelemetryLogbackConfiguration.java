package de.trettstadt.microservices.springbootmicroservice.common.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the OpenTelemetry SDK to the Logback {@link OpenTelemetryAppender}
 * so that application logs are exported via OTLP alongside traces and metrics.
 */
@Configuration
public class OpenTelemetryLogbackConfiguration {

  private final OpenTelemetry openTelemetry;

  OpenTelemetryLogbackConfiguration(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  @PostConstruct
  void init() {
    OpenTelemetryAppender.install(openTelemetry);
  }
}
