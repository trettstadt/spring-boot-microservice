#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=== Phase 1: Provisioning infrastructure (Hetzner Cloud) ==="
cd "$SCRIPT_DIR/infrastructure"
pulumi up --yes

echo "=== Retrieving server IPs ==="
MASTER_IP=$(pulumi stack output masterPublicIp)
WORKER_IP=$(pulumi stack output workerPublicIp)

echo "Master IP: $MASTER_IP"
echo "Worker IP: $WORKER_IP"

echo "=== Generating Ansible inventory ==="
cat > "$SCRIPT_DIR/ansible/inventory.ini" <<EOF
[master]
k3s-master ansible_host=${MASTER_IP} ansible_user=root

[workers]
k3s-worker ansible_host=${WORKER_IP} ansible_user=root

[k3s_cluster:children]
master
workers
EOF

echo "=== Phase 2: Bootstrapping K3s with Ansible ==="
cd "$SCRIPT_DIR/ansible"
ANSIBLE_HOST_KEY_CHECKING=false ansible-playbook -i inventory.ini playbook.yml

echo "=== Phase 3: Installing persistent storage driver ==="
bash "$SCRIPT_DIR/scripts/setup-storage.sh"

echo "=== Phase 4: Deploying all Helm charts via Helmfile ==="
cd "$SCRIPT_DIR"
KUBECONFIG="$HOME/.kube/config-k3s-demo" helmfile --environment prod sync --wait

echo "=== Phase 5: Creating Let's Encrypt ClusterIssuer ==="
KUBECONFIG="$HOME/.kube/config-k3s-demo" kubectl apply -f - <<'EOF'
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: admin@retdemo.de
    privateKeySecretRef:
      name: letsencrypt-prod-account-key
    solvers:
      - http01:
          ingress:
            class: traefik
EOF

echo "=== Phase 6: Configuring database ==="
bash "$SCRIPT_DIR/scripts/setup-database.sh"

echo "=== Phase 7: Configuring Keycloak (clients, scopes) ==="
bash "$SCRIPT_DIR/scripts/setup-keycloak.sh"

echo ""

echo "=== DNS zone ==="
cd "$SCRIPT_DIR/infrastructure"
DNS_ZONE_ID=$(pulumi stack output dnsZoneId 2>/dev/null || echo "not available")
DNS_ZONE_NAME=$(pulumi stack output dnsZoneName 2>/dev/null || echo "retdemo.de")
echo "Zone: $DNS_ZONE_NAME (id: $DNS_ZONE_ID)"
echo "Wildcard: *.retdemo.de -> $MASTER_IP"
echo ""

echo "=== Deployment complete ==="
echo "Master IP: $MASTER_IP"
echo "Worker IP: $WORKER_IP"
echo "Keycloak: https://auth.retdemo.de"
echo "Kubeconfig: ~/.kube/config-k3s-demo"

echo ""
echo "=== Booking API ==="
echo "Booking: https://booking.retdemo.de"
