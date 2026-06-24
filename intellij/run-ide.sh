#!/usr/bin/env bash
# Launch a sandbox IDE with the Format Incrementer plugin loaded.
set -euo pipefail
cd "$(dirname "$0")/.."
# shellcheck source=scripts/ensure-jdk.sh
source ./scripts/ensure-jdk.sh
exec ./gradlew runIde "$@"
