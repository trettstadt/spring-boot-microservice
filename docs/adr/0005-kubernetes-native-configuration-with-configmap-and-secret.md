# 5. Kubernetes-Native Configuration with ConfigMap and Secret

Date: 2026-06-02

## Status

Accepted

## Context

The Spring Boot microservice needs configuration that differs between environments (local development, staging, production). The configuration includes both non-sensitive values (datasource URL, OAuth2 issuer URI, actuator settings, OpenTelemetry endpoints) and sensitive secrets (database password, OAuth2 client secret).

Several approaches exist for supplying configuration to a Spring Boot application running in Kubernetes:

- **Environment variables**: Simple and widely used. Spring Boot maps `SPRING_DATASOURCE_URL` etc. automatically. However, nested YAML structures become awkward with flat environment variable names, and there is no built-in layering or override mechanism. Many environment variables per pod leads to cluttered deployment manifests.

- **Dedicated config server** (Spring Cloud Config): Provides a centralized configuration repository with versioning, encryption, and dynamic refresh. However, it introduces a significant infrastructure dependency — a separate stateful service that must be deployed, secured, and maintained. For a single microservice (or a small number of services), the operational overhead outweighs the benefits.

- **Kubernetes ConfigMaps and Secrets mounted as files**: ConfigMaps hold non-sensitive values, Secrets hold sensitive ones, both mounted as files into the pod. Spring Boot's `spring.config.import` mechanism loads `application.yaml` from the mounted directories, supporting the same structured YAML format used in the source tree.

We also required that the configuration mechanism:

1. Support the same structured YAML format used in local development (`application.yaml`, `application-localdev.yaml`), so no mental model switch is needed between local and deployed environments.
2. Make it clear which values are non-sensitive (commit-safe) and which are secrets (must never appear in version control or CI logs).
3. Allow secret values to be sourced from an external keystore (AWS Secrets Manager, GCP Secret Manager, HashiCorp Vault) without changing the application code or configuration model.
4. Fall back gracefully when external secret management is not available (e.g., local development clusters).

## Decision

We will supply configuration to the application via **two Kubernetes resources, each containing a complete `application.yaml` file, mounted as volumes and loaded through Spring Boot's `spring.config.import`**:

1. **ConfigMap** (`{name}-config`): Contains non-sensitive configuration — datasource URL and username, OAuth2 issuer URI and client ID, actuator settings, OpenTelemetry export endpoints, and the active Spring profile. This is rendered from the Helm chart template and its values are sourced from the chart's `values.yaml` or environment-specific overrides.

2. **Secret** (`{name}-secret`): Contains sensitive configuration — currently the database password. It is also structured as an `application.yaml` file. The Secret is managed by the External Secrets Operator (see ADR 0006) when enabled, or can be created manually for local development. The ConfigMap references the Secret via `spring.config.import: "optional:file:/etc/config-secret/application.yaml"`.

### How it works

- The Deployment mounts the ConfigMap at `/etc/config/` and the Secret at `/etc/config-secret/` (when ESO is enabled).
- An environment variable `SPRING_CONFIG_IMPORT=optional:file:/etc/config/application.yaml` points Spring Boot to the ConfigMap file as the primary external config source.
- The ConfigMap's own `application.yaml` contains a secondary `spring.config.import: "optional:file:/etc/config-secret/application.yaml"`, which loads the Secret values on top, overriding only the sensitive keys.
- Both `optional:file:` prefixes ensure the application starts even if one source is missing — critical for the Secret mount when ESO is not installed.

### Layering order

Configuration is resolved in this order (later sources override earlier ones):

1. Packaged `application.yaml` (in JAR) — minimal, contains only `spring.application.name`.
2. Packaged `application-{profile}.yaml` (in JAR) — localdev defaults.
3. ConfigMap `/etc/config/application.yaml` — environment-specific non-sensitive config.
4. Secret `/etc/config-secret/application.yaml` — sensitive secrets (optional).

This layering mirrors the Spring Boot configuration precedence model and keeps each source focused on its responsibility.

## Consequences

### Positive

- **Structured YAML throughout**: Developers write the same YAML format locally and in Helm values. No need to flatten nested config into environment variable naming conventions.
- **Clear separation of concerns**: ConfigMap contains only commit-safe values; secrets are explicitly separated and can be managed by dedicated tooling (ESO, Vault, etc.).
- **Minimal infrastructure dependencies**: No config server to deploy, secure, or monitor. The entire configuration model relies only on standard Kubernetes primitives (ConfigMap, Secret) plus ESO (optional).
- **Graceful degradation**: The `optional:file:` prefix in both `SPRING_CONFIG_IMPORT` and the ConfigMap's import means the application starts even without the Secret mount — ideal for local dev clusters without ESO.
- **Auditability**: Because ConfigMap contents are rendered from Helm values, non-sensitive configuration is version-controlled alongside the chart. Secret values remain external.
- **Familiar tooling**: Operators debug configuration by `kubectl exec` and inspecting the mounted files — no need to query a config server API.

### Negative

- **Dynamic refresh requires additional machinery**: Spring Boot provides an `/actuator/refresh` endpoint that, when triggered, re-initializes `@RefreshScope`-annotated beans from the current `Environment`. The [`spring-cloud-kubernetes-configuration-watcher`](https://github.com/spring-cloud/spring-cloud-kubernetes/tree/main/spring-cloud-kubernetes-configuration-watcher) project automates this by watching ConfigMaps and Secrets via the Kubernetes API and calling the refresh endpoint on changes. However, this project loads configuration through `spring.config.import` from mounted files — the file contents are read once at startup. The standard `PropertySource` implementations for file-based imports do not re-read the files when `/actuator/refresh` is called. To support dynamic refresh with this approach, you would need to either:

  1. Switch to Spring Cloud Kubernetes's `ConfigMapPropertySource` / `SecretsPropertySource` (which use the Kubernetes API instead of file mounts and support change notifications), or
  2. Integrate `spring-cloud-kubernetes-configuration-watcher` and accept the additional dependency and operational surface.

  For this service, configuration changes are infrequent enough that **restart-based updates are the accepted trade-off**. A rolling update (`kubectl rollout restart`) or Helm upgrade propagates config changes reliably without adding Spring Cloud Kubernetes dependencies or a watcher component to the deployment. If dynamic refresh becomes a requirement in the future, migrating to Spring Cloud Kubernetes property sources is the recommended path.

- **Helm template complexity**: The ConfigMap template duplicates the structure of `application.yaml` as Helm template directives, making it harder to read than a static file. This is mitigated by keeping the ConfigMap template focused and well-documented.
- **Size limits**: ConfigMaps and Secrets are limited to 1 MiB each. This is more than sufficient for a microservice's configuration but would be a constraint for larger applications.
