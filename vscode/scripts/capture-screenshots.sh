#!/usr/bin/env bash
# Capture real VS Code/Cursor Extension Development Host screenshots (macOS).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT/images"
DEMO="$ROOT/.demo-workspace"
EXT_PATH="$ROOT"
CODE="${CODE:-/usr/local/bin/code}"
PROCESS="Cursor"

mkdir -p "$OUT"

edh_window_bounds() {
  osascript <<EOF
tell application "System Events"
  tell process "$PROCESS"
    repeat with w in windows
      if name of w contains "Extension Development Host" then
        set p to position of w
        set s to size of w
        return (item 1 of p as text) & "," & (item 2 of p as text) & "," & (item 1 of s as text) & "," & (item 2 of s as text)
      end if
    end repeat
  end tell
end tell
EOF
}

capture_window() {
  local name="$1"
  local outfile="$2"
  sleep 1
  local bounds
  bounds=$(edh_window_bounds)
  if [[ -z "$bounds" || "$bounds" == "missing value" ]]; then
    echo "ERROR: Extension Development Host window not found for $name" >&2
    return 1
  fi
  IFS=',' read -r x y w h <<< "$bounds"
  screencapture -x -R"$x,$y,$w,$h" "$outfile"
  echo "Captured $name -> $outfile ($(wc -c < "$outfile") bytes)"
}

focus_edh() {
  osascript <<EOF
tell application "$PROCESS" to activate
delay 0.4
tell application "System Events"
  tell process "$PROCESS"
    set frontmost to true
    repeat with w in windows
      if name of w contains "Extension Development Host" then
        perform action "AXRaise" of w
        exit repeat
      end if
    end repeat
  end tell
end tell
EOF
}

close_edh() {
  focus_edh 2>/dev/null || true
  osascript <<'EOF' || true
tell application "System Events"
  tell process "Cursor"
    set frontmost to true
    keystroke "w" using {command down, shift down}
  end tell
end tell
EOF
  sleep 1
}

open_demo_file() {
  local file="$1"
  focus_edh
  osascript <<EOF
tell application "System Events"
  tell process "$PROCESS"
    keystroke "p" using {command down}
    delay 0.8
    keystroke "$file"
    delay 1.0
    key code 36
    delay 1.2
  end tell
end tell
EOF
}

hide_panels() {
  focus_edh
  osascript <<'EOF'
tell application "System Events"
  tell process "Cursor"
    -- hide secondary sidebar (Agents) and bottom panel if open
    keystroke "j" using {command down}
    delay 0.2
    keystroke "b" using {command down, option down}
    delay 0.3
  end tell
end tell
EOF
}

add_three_carets() {
  focus_edh
  osascript <<'EOF'
tell application "System Events"
  tell process "Cursor"
    -- end of line 1
    key code 124 using {command down}
    delay 0.2
    -- add cursor below twice (Cmd+Option+Down)
    key code 125 using {command down, option down}
    delay 0.15
    key code 125 using {command down, option down}
    delay 0.5
  end tell
end tell
EOF
}

close_palette() {
  osascript -e 'tell application "System Events" to key code 53'
  sleep 0.4
}

right_click_editor() {
  focus_edh
  local bounds
  bounds=$(osascript <<'EOF'
tell application "System Events"
  tell process "Cursor"
    repeat with w in windows
      if name of w contains "Extension Development Host" then
        set p to position of w
        set s to size of w
        return (item 1 of p as text) & "," & (item 2 of p as text) & "," & (item 1 of s as text) & "," & (item 2 of s as text)
      end if
    end repeat
  end tell
end tell
EOF
)
  IFS=',' read -r wx wy ww wh <<< "$bounds"
  local cx=$((wx + ww * 55 / 100))
  local cy=$((wy + wh * 45 / 100))
  if command -v cliclick >/dev/null 2>&1; then
    cliclick "rc:$cx,$cy"
  else
    osascript <<EOF
tell application "System Events"
  tell process "$PROCESS"
    set frontmost to true
    -- fallback: open command palette path for context is skipped without cliclick
  end tell
end tell
EOF
    return 1
  fi
  sleep 0.8
}

close_edh

echo "Launching Extension Development Host..."
"$CODE" --extensionDevelopmentPath="$EXT_PATH" -g "$DEMO/demo.txt:1:6" "$DEMO/demo.code-workspace" --new-window
sleep 12
focus_edh
hide_panels

# 1. Multi-carets before
hide_panels
add_three_carets
capture_window "multi-carets" "$OUT/screenshot-multi-carets.png"

# 2. After apply
open_demo_file "demo-after.txt"
hide_panels
capture_window "after-apply" "$OUT/screenshot-after-apply.png"

# 3. Command palette
open_demo_file "demo.txt"
hide_panels
add_three_carets
focus_edh
osascript <<'EOF'
tell application "System Events"
  tell process "Cursor"
    keystroke "p" using {command down, shift down}
    delay 0.8
    keystroke "Format Incrementer"
    delay 1.2
  end tell
end tell
EOF
capture_window "command-palette" "$OUT/screenshot-command-palette.png"

# 4. Pattern quick pick (accept Fill Carets command)
osascript <<'EOF'
tell application "System Events"
  tell process "Cursor"
    key code 36
    delay 1.5
  end tell
end tell
EOF
capture_window "pattern-quickpick" "$OUT/screenshot-pattern-quickpick.png"

# 5. Start input (select Numeric)
osascript <<'EOF'
tell application "System Events"
  tell process "Cursor"
    key code 36
    delay 1.2
  end tell
end tell
EOF
capture_window "start-input" "$OUT/screenshot-start-input.png"

# 6. Context menu
close_palette
open_demo_file "demo.txt"
if right_click_editor; then
  capture_window "context-menu" "$OUT/screenshot-context-menu.png"
else
  echo "WARN: install cliclick (brew install cliclick) for context menu shot; skipping." >&2
fi

close_edh
echo "Screenshot capture complete."
