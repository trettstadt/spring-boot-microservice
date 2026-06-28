#!/usr/bin/env bash
set -euo pipefail

KUBECONFIG="${KUBECONFIG:-$HOME/.kube/config-k3s-demo}"

echo "=== Installing Hetzner CSI driver ==="

if [ -z "${HCLOUD_TOKEN:-}" ]; then
  echo "ERROR: HCLOUD_TOKEN environment variable is not set"
  exit 1
fi

# Create the API token secret
kubectl --kubeconfig "$KUBECONFIG" create secret generic hcloud \
  --namespace kube-system \
  --from-literal=token="$HCLOUD_TOKEN" \
  --dry-run=client -o yaml | kubectl --kubeconfig "$KUBECONFIG" apply -f -

# Install the CSI driver
kubectl --kubeconfig "$KUBECONFIG" apply -f https://raw.githubusercontent.com/hetznercloud/csi-driver/v2.9.0/deploy/kubernetes/hcloud-csi.yml

# Wait for CSI controller to be ready
echo "Waiting for CSI controller..."
kubectl --kubeconfig "$KUBECONFIG" wait --for=condition=ready --timeout=60s pod \
  -n kube-system -l app=hcloud-csi-controller

echo "=== Hetzner CSI driver installed ==="
