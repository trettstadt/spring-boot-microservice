# 8. OpenTelemetry for Observability

Date: 2026-06-02

## Status

Accepted

## Context

The microservice needs observability — the ability to understand its internal state and behavior in production through metrics, traces, and logs. As a production-hardened template, it must provide observability out of the box without requiring application-level code changes for each new endpoint.

Spring Boot provides several observability mechanisms:

- **Micrometer**: Built-in metrics collection (JVM, datasource, HTTP server) with exporters for Prometheus, Datadog, and others. Included in Spring Boot Actuator.
- **Spring Cloud Sleuth** (deprecated): Distributed tracing via trace ID propagation. Superseded by Micrometer Tracing.
- **Micrometer Tracing**: The modern Spring Boot tracing abstraction, backed by OpenTelemetry or Zipkin.
- **OpenTelemetry SDK**: The vendor-neutral standard for observability data. Supports traces, metrics, and logs through a single agent or SDK.

The project also uses several infrastructure components that benefit from observability integration — the PostgreSQL datasource, the OAuth2-secured REST client to the Rooms service, and the Actuator health endpoints.

## Decision

We will use **OpenTelemetry** (OTEL) as the unified observability standard, integrated through Micrometer and Micrometer Tracing, with an OTLP exporter for all three signals (traces, metrics, logs).

### Implementation

1. **Dependencies**: The project includes Micrometer Tracing with OpenTelemetry bridge (`micrometer-tracing-bridge-otel`), OpenTelemetry exporter (`opentelemetry-exporter-otlp`), and Micrometer registry for Prometheus (`micrometer-registry-prometheus`).

2. **Configuration**: All OTEL export settings (endpoint, transport protocol, enabled flags) are configured through the Helm chart's ConfigMap (see ADR 0005). They are disabled by default in the chart but pre-configured for local development in `application-localdev.yaml`.

3. **Traces**: Micrometer Tracing automatically instruments:
   - HTTP server requests (incoming via controllers)
   - HTTP client requests (outgoing via RestClient to Rooms service)
   - The OTLP exporter sends traces to a configurable gRPC endpoint (e.g., Grafana Tempo, Jaeger, SigNoz).

4. **Metrics**: Micrometer collects:
   - JVM metrics (heap, GC, threads)
   - Datasource metrics (connection pool usage, query timing)
   - HTTP server metrics (request count, latency distribution)
   - Prometheus scraping is enabled at `/actuator/prometheus`.
   - Optionally exported via OTLP to an OpenTelemetry Collector.

5. **Logs**: Logging (via Logback) can be configured to export via OTLP, enabling correlation between log entries and trace IDs without sidecar log shippers.

6. **Datasource instrumentation**: The PostgreSQL datasource is instrumented via Micrometer's datasource metrics and the OTEL JDBC instrumentation, providing query-level visibility.

### Why not a sidecar-based approach

An alternative would be running an OpenTelemetry Collector as a sidecar container and exporting via OTLP locally. This decouples export destination configuration from the application but adds another container per pod. We chose the direct export approach because:
- The application already depends on Micrometer and Micrometer Tracing, making OTEL integration a matter of configuration.
- A sidecar adds operational overhead (resource usage, lifecycle management) without clear benefit for a single-service deployment.
- When an OTEL Collector is desired (for batching, retry, or multi-destination routing), it can be deployed as a DaemonSet or standalone deployment — the application's OTLP endpoint is already configurable.

## Consequences

### Positive

- **Single standard for all signals**: Traces, metrics, and logs all export via OTLP. This simplifies the observability stack and aligns with the industry direction (OpenTelemetry as CNCF incubating project).
- **Automatic instrumentation**: Micrometer Tracing traces HTTP requests in and out without any application code. Developers get distributed tracing for free on new endpoints and external calls.
- **Correlated observability**: Trace IDs are propagated across service boundaries via the OAuth2 client (Rooms service calls) and can be correlated with log entries, enabling end-to-end request debugging.
- **Configurable at deploy time**: All OTEL settings (endpoint, enabled/disabled) are in the Helm values and ConfigMap, so operators can enable observability per environment without rebuilding the application.
- **Prometheus compatibility**: Metrics are simultaneously available via the Prometheus scrape endpoint, supporting both pull-based (Prometheus) and push-based (OTLP) metric collection.

### Negative

- **Dependency footprint**: The OpenTelemetry exporter and bridge add approximately 10-15 MB to the JAR and increase startup classpath scanning time.
- **Configuration surface**: With separate flags for traces, metrics, and logs export (each with their own endpoint and transport settings), the configuration matrix is large. Defaults are chosen to be safe (all disabled), but operators must understand which combination they need.
- **Resource overhead**: Tracing instrumentation adds marginal CPU and memory overhead per request. For high-throughput services, sampling strategies should be configured. This is not yet configured and would be a production-readiness task.
- **Log export maturity**: OTLP log export is less mature than trace/metric export. The log export path is included for forward compatibility but may require additional configuration for production use.
