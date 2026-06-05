# 7. External Secrets Operator for Database Credential Management

Date: 2026-06-02

## Status

Accepted

## Context

The microservice requires a database password to connect to PostgreSQL. This credential must be stored securely and made available to the application at runtime. It must never appear in version control, CI logs, or container images.

Options for managing this secret in Kubernetes:

- **Hardcoded Kubernetes Secret**: Created manually with `kubectl create secret` or templated in Helm. Secrets are only base64-encoded, not encrypted at rest unless etcd encryption is configured. The raw value still exists in Helm release history, shell history, and any CI pipeline logs.

- **Sealed Secrets**: Bitnami Sealed Secrets encrypts the secret into a SealedSecret CRD that can be committed to Git. Decryption happens inside the cluster. This makes the encrypted secret auditable in Git but requires managing encryption keys and re-sealing on rotation.

- **External Secrets Operator (ESO)**: A Kubernetes operator that synchronizes secrets from an external keystore (AWS Secrets Manager, GCP Secret Manager, HashiCorp Vault, Azure Key Vault) into native Kubernetes Secrets. The external store is the source of truth; the operator maintains the in-cluster copy. ESO supports templating, refresh intervals, and deletion policies.

- **Vault with CSI driver**: HashiCorp Vault can inject secrets via a CSI volume mount without creating Kubernetes Secret objects. This avoids storing the secret in Kubernetes entirely but requires Vault to be deployed and managed, and adds CSI driver complexity.

The project aims to be deployment-agnostic — the same Helm chart should work in a local Docker Desktop cluster, a staging environment with AWS Secrets Manager, and a production environment with GCP Secret Manager.

## Decision

We will use **External Secrets Operator (ESO)** to manage the database password, with a clear opt-in/opt-out mechanism.

### How it works

1. ESO is **disabled by default** (`externalSecrets.enabled: false`). In this mode, no Secret volume is mounted, and the ConfigMap's `spring.config.import` references the secret file path with the `optional:file:` prefix, allowing the application to start without it. This supports local development clusters where ESO is not installed.

2. When enabled (`externalSecrets.enabled: true`), the Helm chart creates:
   - A **SecretStore** (or ClusterSecretStore) pointing to the external provider — configured through `.Values.externalSecrets.store.spec`.
   - An **ExternalSecret** that defines a template producing an `application.yaml` with the database password, fetched from the external store's remote key.

3. The ExternalSecret template wraps the fetched password into the same `application.yaml` structure that the ConfigMap expects:
   ```yaml
   spring:
     datasource:
       password: "{{ .password }}"
   ```

4. The Deployment conditionally mounts the resulting Secret as a volume at `/etc/config-secret/`, alongside the ConfigMap at `/etc/config/`.

### Provider abstraction

The `store.spec` is passed as raw YAML from Helm values, making ESO provider configuration entirely the responsibility of the deployment environment. Local development uses the [ESO fake provider](https://external-secrets.io/latest/provider/fake/) (see `values-local.yaml`), while production environments use AWS, GCP, Azure, or Vault providers without chart changes.

### Why not Sealed Secrets

Sealed Secrets would require the secret value to be committed to Git (in encrypted form) and re-sealed on every rotation. ESO keeps the secret value entirely outside the cluster and Git — the external store is the authoritative source, and the operator syncs it. This aligns better with the GitOps principle of keeping secrets out of the repository, even in encrypted form.

## Consequences

### Positive

- **Secret never in version control**: The database password exists only in the external keystore. No encrypted secret objects are committed to Git.
- **Provider-agnostic**: The same chart works with AWS, GCP, Azure, Vault, or the local fake provider — only the `store.spec` changes.
- **Automatic refresh**: ESO refreshes secrets at a configurable interval (default: 1 hour). If the password is rotated in the external store, ESO updates the Kubernetes Secret, and a subsequent pod restart picks up the new value.
- **Graceful fallback**: When ESO is disabled (default), the application starts without the Secret mount. This enables local development without any external dependency.
- **Templating**: ESO's template engine lets us transform the raw secret (a JSON object with a `password` field) into the `application.yaml` structure that Spring Boot expects, maintaining consistency with the ConfigMap approach (ADR 0005).

### Negative

- **Operational dependency**: ESO must be installed in the cluster before the chart can use it. This adds a cluster-level prerequisite. The NOTES.txt template reminds operators of this.
- **CRD management**: ESO installs CRDs (SecretStore, ExternalSecret) that must be managed during upgrades. The README documents the installation procedure.
- **Secret exists in Kubernetes**: Even with ESO, the resolved secret value is stored in a standard Kubernetes Secret object (though etcd encryption can mitigate this). The Vault CSI driver avoids this but adds complexity that is not justified for a single database password.
- **Refresh ≠ hot-reload**: ESO refreshes the Kubernetes Secret, but Spring Boot does not hot-reload configuration from mounted files. A rolling update or pod restart is required to pick up the new value. This is consistent with ADR 0005's restart-based configuration model.
