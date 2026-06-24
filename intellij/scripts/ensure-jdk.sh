#!/usr/bin/env bash
# Ensures a JDK 21 install exists for running Gradle (Gradle 8.x cannot run on JDK 25+).
# Downloads Temurin 21 into intellij/.jdk/ on first run.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
JDK_MARKER="$PROJECT_DIR/.jdk/home/bin/java"

if [[ -x "$JDK_MARKER" ]]; then
  export JAVA_HOME="$PROJECT_DIR/.jdk/home"
  exit 0
fi

OS="$(uname -s)"
ARCH="$(uname -m)"
case "$OS-$ARCH" in
  Darwin-arm64)  PLATFORM="mac/aarch64" ;;
  Darwin-x86_64) PLATFORM="mac/x64" ;;
  Linux-aarch64) PLATFORM="linux/aarch64" ;;
  Linux-x86_64)  PLATFORM="linux/x64" ;;
  *)
    echo "Unsupported platform: $OS $ARCH" >&2
    echo "Install JDK 21 manually and set JAVA_HOME, then re-run ./gradlew" >&2
    exit 1
    ;;
esac

DOWNLOAD_URL="https://api.adoptium.net/v3/binary/latest/21/ga/${PLATFORM}/jdk/hotspot/normal/eclipse?project=jdk"
TMP_ARCHIVE="$(mktemp "${TMPDIR:-/tmp}/format-incrementer-jdk.XXXXXX.tar.gz")"

cleanup() { rm -f "$TMP_ARCHIVE"; }
trap cleanup EXIT

echo "Downloading Temurin JDK 21 for Gradle (one-time setup)..."
curl -fsSL -o "$TMP_ARCHIVE" "$DOWNLOAD_URL"

echo "Extracting to $PROJECT_DIR/.jdk ..."
rm -rf "$PROJECT_DIR/.jdk"
mkdir -p "$PROJECT_DIR/.jdk"
tar -xzf "$TMP_ARCHIVE" -C "$PROJECT_DIR/.jdk" --strip-components=0

# Normalize layout: adoptium tarball is jdk-21.x+.../Contents/Home on Mac, jdk-21.../ on Linux
if [[ -d "$PROJECT_DIR/.jdk/jdk-"*"/Contents/Home" ]]; then
  ln -sfn "$(find "$PROJECT_DIR/.jdk" -maxdepth 2 -type d -path '*/Contents/Home' | head -1)" "$PROJECT_DIR/.jdk/home"
elif [[ -d "$PROJECT_DIR/.jdk/jdk-"* ]]; then
  ln -sfn "$(find "$PROJECT_DIR/.jdk" -maxdepth 1 -type d -name 'jdk-*' | head -1)" "$PROJECT_DIR/.jdk/home"
else
  echo "Unexpected JDK archive layout" >&2
  exit 1
fi

export JAVA_HOME="$PROJECT_DIR/.jdk/home"
echo "JDK 21 ready at $JAVA_HOME"
