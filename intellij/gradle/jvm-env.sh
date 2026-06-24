# Gradle 8.x cannot run on JDK 25+. Auto-select JDK 21 when needed.
# Override with FORMAT_INCREMENTER_JAVA_HOME or JAVA_HOME if you prefer a specific JDK.

find_compatible_jdk() {
  if [ -n "${FORMAT_INCREMENTER_JAVA_HOME:-}" ] && [ -x "${FORMAT_INCREMENTER_JAVA_HOME}/bin/java" ]; then
    printf '%s\n' "$FORMAT_INCREMENTER_JAVA_HOME"
    return 0
  fi

  for candidate in \
    "$APP_HOME/.jdk/home" \
    "$APP_HOME/.jdk"/*/Contents/Home \
    "${HOME}/.local/jdks"/*/Contents/Home \
    /Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home \
    /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home \
    /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home \
    /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
  do
    if [ -x "$candidate/bin/java" ]; then
      printf '%s\n' "$candidate"
      return 0
    fi
  done

  if command -v /usr/libexec/java_home >/dev/null 2>&1; then
    jdk=$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home -v 17 2>/dev/null || true)
    if [ -n "$jdk" ] && [ -x "$jdk/bin/java" ]; then
      printf '%s\n' "$jdk"
      return 0
    fi
  fi

  return 1
}

java_major_version() {
  java_cmd="$1"
  version=$("$java_cmd" -version 2>&1 | head -n 1)
  major=$(printf '%s' "$version" | sed -n 's/.*version "\([0-9][0-9]*\).*/\1/p')
  if [ -n "$major" ]; then
    printf '%s' "$major"
    return 0
  fi
  # Java 8 reports as 1.8
  major=$(printf '%s' "$version" | sed -n 's/.*version "1\.\([0-9][0-9]*\).*/\1/p')
  printf '%s' "$major"
}

resolve_java_cmd() {
  if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
    printf '%s\n' "${JAVA_HOME}/bin/java"
  else
    printf '%s\n' "java"
  fi
}

current_java=$(resolve_java_cmd)
current_major=$(java_major_version "$current_java")

if [ -z "$current_major" ] || [ "$current_major" -ge 25 ]; then
  compatible_jdk=$(find_compatible_jdk) || true
  if [ -n "$compatible_jdk" ]; then
    export JAVA_HOME="$compatible_jdk"
  else
    echo "ERROR: Gradle cannot run on JDK ${current_major:-unknown}." >&2
    echo "Install JDK 21, set JAVA_HOME, or run: ./scripts/ensure-jdk.sh" >&2
    exit 1
  fi
fi
