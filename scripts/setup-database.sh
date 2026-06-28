#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-booking}"
DB_NAME="${DB_NAME:-microservicedb}"
DB_USER="${DB_USER:-appuser}"
KUBECONFIG="${KUBECONFIG:-$HOME/.kube/config-k3s-demo}"

echo "=== PostgreSQL setup ==="

POD=$(kubectl --kubeconfig "$KUBECONFIG" get pods -n "$NAMESPACE" -l app.kubernetes.io/name=postgresql -o name | head -1 | sed 's|pod/||')
if [ -z "$POD" ]; then
  echo "ERROR: No PostgreSQL pod found"
  exit 1
fi

PGPASS=$(kubectl --kubeconfig "$KUBECONFIG" get secret -n "$NAMESPACE" postgresql -o jsonpath='{.data.postgres-password}' | base64 -d)

psql() {
  kubectl --kubeconfig "$KUBECONFIG" exec -n "$NAMESPACE" "$POD" -i -- env PGPASSWORD="$PGPASS" psql "$@"
}

echo "Ensuring database '$DB_NAME' exists..."
EXISTS=$(psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME'" 2>/dev/null || true)
if [ "$EXISTS" = "1" ]; then
  echo "Database '$DB_NAME' already exists"
else
  psql -U postgres -c "CREATE DATABASE $DB_NAME"
  echo "Created database '$DB_NAME'"
fi

echo "Granting privileges..."
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER"
psql -U postgres -d "$DB_NAME" -c "GRANT ALL ON SCHEMA public TO $DB_USER"
psql -U postgres -d "$DB_NAME" -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER"
psql -U postgres -d "$DB_NAME" -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER"

echo "Verifying connection as '$DB_USER'..."
APP_PASS=$(kubectl --kubeconfig "$KUBECONFIG" get secret -n "$NAMESPACE" postgresql -o jsonpath='{.data.password}' | base64 -d)
VERIFY=$(kubectl --kubeconfig "$KUBECONFIG" exec -n "$NAMESPACE" "$POD" -i -- env PGPASSWORD="$APP_PASS" psql -U "$DB_USER" -d "$DB_NAME" -tAc "SELECT current_database() || ' as ' || current_user" 2>/dev/null || echo "failed")
echo "Connected to: $VERIFY"

echo "=== PostgreSQL setup complete ==="
