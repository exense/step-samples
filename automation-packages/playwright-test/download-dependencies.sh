b#!/usr/bin/env bash
# Downloads the Step system keyword library required to build and execute the automation package.

set -euo pipefail

JAR_VERSION="1.0.30"
JAR_NAME="step-library-kw-system-${JAR_VERSION}.jar"
JAR_URL="https://repo1.maven.org/maven2/ch/exense/step/library/step-library-kw-system/${JAR_VERSION}/${JAR_NAME}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEST="${SCRIPT_DIR}/${JAR_NAME}"

if [ -f "${DEST}" ]; then
  echo "${JAR_NAME} already exists, skipping download."
  exit 0
fi

echo "Downloading ${JAR_NAME} from Maven Central..."

if command -v curl &>/dev/null; then
  curl -fL --progress-bar "${JAR_URL}" -o "${DEST}"
elif command -v wget &>/dev/null; then
  wget --show-progress -q "${JAR_URL}" -O "${DEST}"
else
  echo "ERROR: neither curl nor wget is available. Please install one and retry." >&2
  exit 1
fi

echo "Saved to: ${DEST}"