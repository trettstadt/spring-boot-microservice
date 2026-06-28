#!/usr/bin/env bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-booking}"
KUBECONFIG="${KUBECONFIG:-$HOME/.kube/config-k3s-demo}"

echo "=== Keycloak setup ==="

POD=$(kubectl --kubeconfig "$KUBECONFIG" get pods -n "$NAMESPACE" -l app.kubernetes.io/name=keycloakx -o name | head -1 | sed 's|pod/||')

kcexec() {
  kubectl --kubeconfig "$KUBECONFIG" exec -n "$NAMESPACE" "$POD" -- "$@"
}

kcexec /opt/keycloak/bin/kcadm.sh config credentials \
  --server http://localhost:8080/auth \
  --realm master \
  --user admin \
  --password admin >/dev/null 2>&1

# Authenticate for REST API calls
ADMIN_TOKEN=$(curl -sk -X POST -H "Host: auth.retdemo.de" \
  "https://91.99.111.186/auth/realms/master/protocol/openid-connect/token" \
  -d "client_id=admin-cli&username=admin&password=admin&grant_type=password" | python3 -c "import sys,json; print(json.load(sys.stdin)['access_token'])")

KC_BASE="https://auth.retdemo.de/auth/admin/realms/master"
CURL_AUTH=(-sk -H "Host: auth.retdemo.de" -H "Authorization: Bearer $ADMIN_TOKEN" -H "Content-Type: application/json")

echo "Configuring clients..."

# --- spring-boot-microservice ---
UUID=$(kcexec /opt/keycloak/bin/kcadm.sh get clients -r master -q clientId=spring-boot-microservice --fields id | python3 -c "import sys,json; d=json.load(sys.stdin); print(d[0]['id'] if d else '')")
if [ -n "$UUID" ]; then
  echo "Client spring-boot-microservice exists ($UUID)"
else
  echo "Creating client spring-boot-microservice..."
  UUID=$(kcexec /opt/keycloak/bin/kcadm.sh create clients -r master -s clientId=spring-boot-microservice -s publicClient=false -s serviceAccountsEnabled=true -s standardFlowEnabled=false -s directAccessGrantsEnabled=false | sed "s/.*id '\([^']*\)'.*/\1/")
fi

# Ensure RS256 signing algorithm and secret
curl "${CURL_AUTH[@]}" -X PUT "$KC_BASE/clients/$UUID" \
  -d '{"attributes":{"access.token.signed.response.alg":"RS256"},"secret":"1N73bVtOYRUinWUGZ3hEkiwnG04j8Rr9"}'

# Ensure audience mapper
MAPPERS=$(curl "${CURL_AUTH[@]}" "$KC_BASE/clients/$UUID/protocol-mappers/models" | python3 -c "import sys,json; d=json.load(sys.stdin); print([m['name'] for m in d])")
if echo "$MAPPERS" | grep -q "audience-mapper"; then
  echo "Audience mapper exists"
else
  echo "Adding audience mapper..."
  curl "${CURL_AUTH[@]}" -X POST "$KC_BASE/clients/$UUID/protocol-mappers/models" \
    -d '{"name":"audience-mapper","protocol":"openid-connect","protocolMapper":"oidc-audience-mapper","config":{"included.client.audience":"spring-boot-microservice","id.token.claim":"false","access.token.claim":"true","lightweight.claim":"false"}}' >/dev/null
fi

# --- rooms ---
UUID=$(kcexec /opt/keycloak/bin/kcadm.sh get clients -r master -q clientId=rooms --fields id | python3 -c "import sys,json; d=json.load(sys.stdin); print(d[0]['id'] if d else '')")
if [ -n "$UUID" ]; then
  echo "Client rooms exists ($UUID)"
else
  echo "Creating client rooms..."
  UUID=$(kcexec /opt/keycloak/bin/kcadm.sh create clients -r master -s clientId=rooms -s publicClient=false -s serviceAccountsEnabled=true -s standardFlowEnabled=false -s directAccessGrantsEnabled=false | sed "s/.*id '\([^']*\)'.*/\1/")
fi
curl "${CURL_AUTH[@]}" -X PUT "$KC_BASE/clients/$UUID" \
  -d '{"attributes":{"access.token.signed.response.alg":"RS256"},"secret":"secret"}'

# --- bookings scope ---
SCOPES=$(kcexec /opt/keycloak/bin/kcadm.sh get client-scopes -r master --fields name | python3 -c "import sys,json; d=json.load(sys.stdin); print([s['name'] for s in d])")
if echo "$SCOPES" | grep -q "bookings"; then
  echo "Scope bookings exists"
else
  echo "Creating scope bookings..."
  curl "${CURL_AUTH[@]}" -X POST "$KC_BASE/client-scopes" \
    -d '{"name":"bookings","protocol":"openid-connect","attributes":{"display.on.consent.screen":"true","include.in.token.scope":"true"}}' >/dev/null
fi

echo "=== Keycloak setup complete ==="
