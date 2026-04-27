#!/bin/bash
# Converts all PlantUML diagrams (.puml) under docs/ to SVG format.
# Uses the plantuml/plantuml Docker image. Pulls it if not present.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PLANTUML_IMAGE="plantuml/plantuml:latest"
DOCKER_USER=$(id -u):$(id -g)

# Pull image if not present locally
if ! docker image inspect "$PLANTUML_IMAGE" > /dev/null 2>&1; then
  echo "Pulling $PLANTUML_IMAGE..."
  docker pull "$PLANTUML_IMAGE"
fi

# Find all .puml files in docs/ (non-recursive, as per convention)
puml_files=("$SCRIPT_DIR"/*.puml)

if [ ${#puml_files[@]} -eq 0 ] || [ ! -e "${puml_files[0]}" ]; then
  echo "No .puml files found in $SCRIPT_DIR"
  exit 0
fi

for puml in "${puml_files[@]}"; do
  basename="$(basename "$puml")"
  dirname="$(basename "$(dirname "$puml")")"
  echo "Converting $puml → SVG"
  docker run --rm \
    -u "$DOCKER_USER" \
    -v "$SCRIPT_DIR:/docs" \
    plantuml/plantuml \
    -tsvg -o /docs \
    "/docs/$basename"
done

echo "Done."