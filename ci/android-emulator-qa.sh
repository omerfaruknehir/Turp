#!/usr/bin/env bash
set -uo pipefail

ROOT="${GITHUB_WORKSPACE:-$(pwd)}"
OUT="$ROOT/qa-artifacts"
PACKAGE="app.turp.chat.debug"
ACTIVITY="app.turp.chat.MainActivity"
mkdir -p "$OUT"
status=0

record_failure() {
  echo "$1" | tee -a "$OUT/qa-summary.txt" >&2
  status=1
}

capture_screen() {
  local name="$1"
  local attempt
  local ui="$OUT/${name}-ui.xml"
  local tmp="$OUT/${name}-ui.tmp"
  local err="$OUT/${name}-ui-error.txt"
  : > "$err"
  for attempt in 1 2 3 4 5; do
    adb exec-out uiautomator dump /dev/tty > "$tmp" 2>> "$err" || true
    if grep -q '<hierarchy' "$tmp" 2>/dev/null; then
      mv "$tmp" "$ui"
      if (( attempt > 1 )); then
        echo "uiHierarchyRetry=PASS screen=$name attempt=$attempt" >> "$OUT/qa-summary.txt"
      fi
      break
    fi
    cp "$tmp" "$ui" 2>/dev/null || true
    echo "uiautomator dump attempt $attempt returned no hierarchy" >> "$err"
    sleep 1
  done
  rm -f "$tmp"
  adb exec-out screencap -p > "$OUT/${name}.png" 2> "$OUT/${name}-screenshot-error.txt" || true
}

pick_node_center() {
  local file="$1"
  local attr="$2"
  local target="$3"
  python3 - "$file" "$attr" "$target" <<'PY'
import html
import re
import sys

path, attr, target = sys.argv[1], sys.argv[2], sys.argv[3]
data = open(path, encoding="utf-8", errors="replace").read()
for tag in re.findall(r"<node\b[^>]*>", data):
    attrs = dict(re.findall(r'([\w-]+)="([^"]*)"', tag))
    if html.unescape(attrs.get(attr, "")) != target:
        continue
    m = re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", attrs.get("bounds", ""))
    if m:
        x1, y1, x2, y2 = map(int, m.groups())
        print((x1 + x2) // 2, (y1 + y2) // 2)
        raise SystemExit(0)
raise SystemExit(1)
PY
}

pick_text_center() {
  pick_node_center "$1" text "$2"
}

pick_text_prefix_center() {
  local file="$1"
  local prefix="$2"
  python3 - "$file" "$prefix" <<'PY_PREFIX'
import html
import re
import sys

path, prefix = sys.argv[1], sys.argv[2]
data = open(path, encoding="utf-8", errors="replace").read()
for tag in re.findall(r"<node\b[^>]*>", data):
    attrs = dict(re.findall(r'([\w-]+)="([^"]*)"', tag))
    text = html.unescape(attrs.get("text", ""))
    if not text.startswith(prefix):
        continue
    m = re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", attrs.get("bounds", ""))
    if m:
        x1, y1, x2, y2 = map(int, m.groups())
        print((x1 + x2) // 2, (y1 + y2) // 2)
        raise SystemExit(0)
raise SystemExit(1)
PY_PREFIX
}

pick_desc_center() {
  pick_node_center "$1" content-desc "$2"
}

tap_text() {
  local file="$1"
  local text="$2"
  local label="$3"
  local xy
  if xy="$(pick_text_center "$file" "$text")"; then
    read -r x y <<<"$xy"
    adb shell input tap "$x" "$y"
    echo "${label}=PASS text=${text} coord=${x},${y}" >> "$OUT/qa-summary.txt"
    return 0
  fi
  record_failure "${label}=FAIL node-not-found text=$text"
  return 1
}

tap_desc() {
  local file="$1"
  local desc="$2"
  local label="$3"
  local xy
  if xy="$(pick_desc_center "$file" "$desc")"; then
    read -r x y <<<"$xy"
    adb shell input tap "$x" "$y"
    echo "${label}=PASS desc=${desc} coord=${x},${y}" >> "$OUT/qa-summary.txt"
    return 0
  fi
  record_failure "${label}=FAIL node-not-found desc=$desc"
  return 1
}

tap_provider_cta() {
  local file="$1"
  local text xy
  for text in "Set up a provider" "Set up provider" "Add provider"; do
    if xy="$(pick_text_center "$file" "$text")"; then
      read -r x y <<<"$xy"
      adb shell input tap "$x" "$y"
      echo "tapProviderCta=PASS text=${text} coord=${x},${y}" >> "$OUT/qa-summary.txt"
      return 0
    fi
  done
  record_failure "tapProviderCta=FAIL no-provider-cta-node"
  return 1

}

scroll_until_text() {
  local prefix="$1"
  local ui="$2"
  local target="$3"
  local max_attempts="${4:-10}"
  local safe_y="${5:-2200}"
  local attempt xy x y scroll_xy sx sy ex ey capture_name

  SCROLL_FOUND_UI="$ui"
  SCROLL_FOUND_X=""
  SCROLL_FOUND_Y=""

  for ((attempt=0; attempt<=max_attempts; attempt++)); do
    xy="$(pick_text_center "$ui" "$target" 2>/dev/null || true)"
    if [[ -n "$xy" ]]; then
      read -r x y <<<"$xy"
      if (( y <= safe_y )); then
        SCROLL_FOUND_UI="$ui"
        SCROLL_FOUND_X="$x"
        SCROLL_FOUND_Y="$y"
        echo "scrollUntilText=PASS target=${target} attempt=${attempt} coord=${x},${y}" >> "$OUT/qa-summary.txt"
        return 0
      fi
    fi

    (( attempt == max_attempts )) && break

    scroll_xy="$(python3 - "$ui" <<'PY_SCROLL'
import re, sys
data = open(sys.argv[1], encoding='utf-8', errors='replace').read()
for tag in re.findall(r'<node\b[^>]*>', data):
    if 'scrollable="true"' not in tag:
        continue
    m = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', tag)
    if not m:
        continue
    x1, y1, x2, y2 = map(int, m.groups())
    x = (x1 + x2) // 2
    print(x, y1 + (y2-y1)*3//4, x, y1 + (y2-y1)//4)
    raise SystemExit(0)
raise SystemExit(1)
PY_SCROLL
    )" || true
    if [[ -z "$scroll_xy" ]]; then
      # Compose's Modifier.verticalScroll does not reliably expose a
      # scrollable=true accessibility node to uiautomator. Fall back to a
      # bounded screen swipe instead of incorrectly treating the page as
      # non-scrollable.
      screen_size="$(adb shell wm size 2>/dev/null | tr -d '\r' | sed -n 's/.*Physical size: \([0-9]*\)x\([0-9]*\).*/\1 \2/p' | head -n1)"
      if [[ -n "$screen_size" ]]; then
        read -r screen_w screen_h <<<"$screen_size"
        sx=$((screen_w / 2))
        sy=$((screen_h * 3 / 4))
        ex=$sx
        ey=$((screen_h / 4))
      else
        sx=540
        sy=1800
        ex=540
        ey=650
      fi
      echo "scrollUntilText=INFO target=${target} attempt=${attempt} mode=screen-fallback coord=${sx},${sy}->${ex},${ey}" >> "$OUT/qa-summary.txt"
    else
      read -r sx sy ex ey <<<"$scroll_xy"
    fi
    adb shell input swipe "$sx" "$sy" "$ex" "$ey" 350
    capture_name="${prefix}-${attempt}"
    sleep 1
    capture_screen "$capture_name"
    dismiss_quickstep_anr "$capture_name" || true
    ui="$OUT/${capture_name}-ui.xml"
  done

  echo "scrollUntilText=FAIL target=${target} reason=not-visible" >> "$OUT/qa-summary.txt"
  return 1
}

dismiss_quickstep_anr() {
  local name="$1"
  local i
  for i in 1 2 3; do
    if ! grep -Fq "Quickstep isn't responding" "$OUT/${name}-ui.xml" 2>/dev/null; then
      return 0
    fi
    echo "quickstepAnrObserved=INFO screen=$name attempt=$i" >> "$OUT/qa-summary.txt"
    local xy
    if xy="$(pick_text_center "$OUT/${name}-ui.xml" "Wait")"; then
      read -r x y <<<"$xy"
      adb shell input tap "$x" "$y"
      sleep 2
      capture_screen "$name"
    else
      return 1
    fi
  done
  ! grep -Fq "Quickstep isn't responding" "$OUT/${name}-ui.xml" 2>/dev/null
}

dismiss_release_dialog() {
  local name="$1"
  if ! grep -Fq 'Open release' "$OUT/${name}-ui.xml" 2>/dev/null; then
    return 0
  fi
  local xy
  if xy="$(pick_text_center "$OUT/${name}-ui.xml" "Later")"; then
    read -r x y <<<"$xy"
    adb shell input tap "$x" "$y"
    echo "releaseDialogDismissed=PASS coord=${x},${y}" >> "$OUT/qa-summary.txt"
    sleep 2
    capture_screen "$name"
    dismiss_quickstep_anr "$name"
    return $?
  fi
  record_failure "releaseDialogDismissed=FAIL Later-node-not-found"
  return 1
}

{
  echo "Turp Android emulator QA"
  echo "commit=${GITHUB_SHA:-unknown}"
  echo "utc=$(date -u +%Y-%m-%dT%H:%M:%SZ)"
} > "$OUT/qa-summary.txt"

adb wait-for-device
adb shell getprop > "$OUT/getprop.txt" 2>&1 || true
adb devices -l > "$OUT/adb-devices.txt" 2>&1 || true

if ./gradlew --no-daemon --stacktrace --max-workers=2 connectedDebugAndroidTest \
    > "$OUT/connectedDebugAndroidTest.txt" 2>&1; then
  echo "instrumentation=PASS" >> "$OUT/qa-summary.txt"
else
  echo "instrumentation=FAIL" >> "$OUT/qa-summary.txt"
  status=1
fi

if ./gradlew --no-daemon --stacktrace --max-workers=2 :app:installDebug \
    > "$OUT/installDebug.txt" 2>&1; then
  echo "installDebug=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "installDebug=FAIL"
fi

adb shell am force-stop "$PACKAGE" >/dev/null 2>&1 || true
adb shell pm clear "$PACKAGE" > "$OUT/pm-clear.txt" 2>&1 || true
adb logcat -c || true

if adb shell am start -W -n "$PACKAGE/$ACTIVITY" > "$OUT/launch.txt" 2>&1; then
  echo "launchCommand=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "launchCommand=FAIL"
fi

sleep 5
capture_screen welcome
if dismiss_quickstep_anr welcome; then
  echo "systemOverlayClear=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "systemOverlayClear=FAIL Quickstep ANR persisted"
fi

if grep -q '<hierarchy' "$OUT/welcome-ui.xml" 2>/dev/null; then
  echo "welcomeUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
else
  record_failure "welcomeUiHierarchy=FAIL"
fi
[[ -s "$OUT/welcome.png" ]] && echo "welcomeScreenshot=PASS" >> "$OUT/qa-summary.txt" \
  || record_failure "welcomeScreenshot=FAIL"

if tap_text "$OUT/welcome-ui.xml" "Skip for now" "tapSkipForNow"; then
  sleep 4
  capture_screen main
  dismiss_quickstep_anr main || record_failure "mainSystemOverlayClear=FAIL"
  dismiss_release_dialog main || true

  if grep -q '<hierarchy' "$OUT/main-ui.xml" 2>/dev/null; then
    echo "mainUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
  else
    record_failure "mainUiHierarchy=FAIL"
  fi
  [[ -s "$OUT/main.png" ]] && echo "mainScreenshot=PASS" >> "$OUT/qa-summary.txt" \
    || record_failure "mainScreenshot=FAIL"

  if tap_provider_cta "$OUT/main-ui.xml"; then
    sleep 3
    capture_screen provider
    dismiss_quickstep_anr provider || record_failure "providerSystemOverlayClear=FAIL"
    if grep -q '<hierarchy' "$OUT/provider-ui.xml" 2>/dev/null; then
      echo "providerUiHierarchy=PASS" >> "$OUT/qa-summary.txt"
    else
      record_failure "providerUiHierarchy=FAIL"
    fi
    [[ -s "$OUT/provider.png" ]] && echo "providerScreenshot=PASS" >> "$OUT/qa-summary.txt" \
      || record_failure "providerScreenshot=FAIL"

    # Provider Back returns to Settings home. Keep scrolling until the Search row is not merely
    # present in the accessibility tree but is also above the bottom system-navigation area.
    adb shell input keyevent 4
    sleep 2
    capture_screen settings-home
    dismiss_quickstep_anr settings-home || record_failure "settingsHomeSystemOverlayClear=FAIL"
    settings_ui="$OUT/settings-home-ui.xml"
    search_ready=false
    search_x=""
    search_y=""
    for scroll_attempt in 1 2 3 4 5 6 7 8; do
      search_xy="$(pick_text_center "$settings_ui" "Search & web" 2>/dev/null || true)"
      if [[ -n "$search_xy" ]]; then
        read -r search_x search_y <<<"$search_xy"
        if (( search_y <= 2200 )); then
          echo "searchNodeVisible=PASS attempt=$scroll_attempt coord=${search_x},${search_y}" >> "$OUT/qa-summary.txt"
          search_ready=true
          break
        fi
        echo "searchNodeClipped=INFO attempt=$scroll_attempt coord=${search_x},${search_y}" >> "$OUT/qa-summary.txt"
      fi
      scroll_xy="$(python3 - "$settings_ui" <<'PY2'
import re, sys
data = open(sys.argv[1], encoding='utf-8', errors='replace').read()
for tag in re.findall(r'<node\b[^>]*>', data):
    if 'scrollable="true"' not in tag:
        continue
    m = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', tag)
    if not m:
        continue
    x1, y1, x2, y2 = map(int, m.groups())
    x = (x1 + x2) // 2
    print(x, y1 + (y2-y1)*3//4, x, y1 + (y2-y1)//4)
    raise SystemExit(0)
raise SystemExit(1)
PY2
      )" || true
      if [[ -z "$scroll_xy" ]]; then
        record_failure "scrollSettingsHome=FAIL scrollable-node-not-found attempt=$scroll_attempt"
        break
      fi
      read -r sx sy ex ey <<<"$scroll_xy"
      adb shell input swipe "$sx" "$sy" "$ex" "$ey" 350
      echo "scrollSettingsHome=PASS attempt=$scroll_attempt coord=${sx},${sy}->${ex},${ey}" >> "$OUT/qa-summary.txt"
      sleep 2
      capture_name="settings-home-scroll-${scroll_attempt}"
      capture_screen "$capture_name"
      dismiss_quickstep_anr "$capture_name" || record_failure "settingsScrollSystemOverlayClear=FAIL attempt=$scroll_attempt"
      settings_ui="$OUT/${capture_name}-ui.xml"
    done

    # The loop captures the result of its final swipe at the end of the last iteration.
    # Re-evaluate that final capture before declaring the Search row unavailable.
    if [[ "$search_ready" != true ]]; then
      search_xy="$(pick_text_center "$settings_ui" "Search & web" 2>/dev/null || true)"
      if [[ -n "$search_xy" ]]; then
        read -r search_x search_y <<<"$search_xy"
        if (( search_y <= 2200 )); then
          echo "searchNodeVisible=PASS attempt=final coord=${search_x},${search_y}" >> "$OUT/qa-summary.txt"
          search_ready=true
        else
          echo "searchNodeClipped=INFO attempt=final coord=${search_x},${search_y}" >> "$OUT/qa-summary.txt"
        fi
      fi
    fi

    if [[ "$search_ready" == true ]]; then
      adb shell input tap "$search_x" "$search_y"
      echo "openSearchSettings=PASS text=Search & web coord=${search_x},${search_y}" >> "$OUT/qa-summary.txt"
      sleep 3
      capture_screen search
      if dismiss_quickstep_anr search; then
        echo "searchSystemOverlayClear=PASS" >> "$OUT/qa-summary.txt"
      else
        record_failure "searchSystemOverlayClear=FAIL Quickstep ANR persisted"
      fi
      if grep -Fq 'Search routing' "$OUT/search-ui.xml" 2>/dev/null && grep -Fq 'Automatic' "$OUT/search-ui.xml" 2>/dev/null; then
        echo "searchSettingsUi=PASS" >> "$OUT/qa-summary.txt"
      else
        record_failure "searchSettingsUi=FAIL expected-compact-search-controls-missing"
      fi
      [[ -s "$OUT/search.png" ]] && echo "searchScreenshot=PASS" >> "$OUT/qa-summary.txt" \
        || record_failure "searchScreenshot=FAIL"
    else
      record_failure "openSearchSettings=FAIL safe-visible-search-row-not-found"
    fi

    # Targeted Developer System Prompt QA. The generic Settings smoke path used
    # to stop before this feature, so prompt-manager/editor regressions could
    # pass CI without ever rendering these screens.
    if [[ "$search_ready" == true ]]; then
      adb shell input keyevent 4
      sleep 2
      capture_screen promptqa-settings-home
      if dismiss_quickstep_anr promptqa-settings-home; then
        echo "promptQaSettingsHomeSystemOverlayClear=PASS" >> "$OUT/qa-summary.txt"
      else
        record_failure "promptQaSettingsHomeSystemOverlayClear=FAIL Quickstep ANR persisted"
      fi

      if scroll_until_text \
          "promptqa-settings-home-scroll" \
          "$OUT/promptqa-settings-home-ui.xml" \
          "About Turp" \
          12; then
        adb shell input tap "$SCROLL_FOUND_X" "$SCROLL_FOUND_Y"
        echo "promptQaOpenAbout=PASS coord=${SCROLL_FOUND_X},${SCROLL_FOUND_Y}" >> "$OUT/qa-summary.txt"
        sleep 2
        capture_screen promptqa-about

        if scroll_until_text \
            "promptqa-about-scroll" \
            "$OUT/promptqa-about-ui.xml" \
            "Developer options" \
            24; then
          adb shell input tap "$SCROLL_FOUND_X" "$SCROLL_FOUND_Y"
          echo "promptQaOpenDeveloper=PASS coord=${SCROLL_FOUND_X},${SCROLL_FOUND_Y}" >> "$OUT/qa-summary.txt"
          sleep 2
          capture_screen promptqa-developer

          if scroll_until_text \
              "promptqa-developer-scroll" \
              "$OUT/promptqa-developer-ui.xml" \
              "Open system prompts" \
              12; then
            adb shell input tap "$SCROLL_FOUND_X" "$SCROLL_FOUND_Y"
            echo "promptQaOpenSystemPrompts=PASS coord=${SCROLL_FOUND_X},${SCROLL_FOUND_Y}" >> "$OUT/qa-summary.txt"
            sleep 2
            capture_screen promptqa-system-prompts
            if dismiss_quickstep_anr promptqa-system-prompts; then
              echo "promptQaSystemPromptsSystemOverlayClear=PASS" >> "$OUT/qa-summary.txt"
            else
              record_failure "promptQaSystemPromptsSystemOverlayClear=FAIL Quickstep ANR persisted"
            fi

            if grep -Fq 'Components' "$OUT/promptqa-system-prompts-ui.xml" 2>/dev/null &&
               grep -Fq 'Sent to model' "$OUT/promptqa-system-prompts-ui.xml" 2>/dev/null &&
               grep -Fq 'Core behavior' "$OUT/promptqa-system-prompts-ui.xml" 2>/dev/null; then
              echo "promptQaManagerUi=PASS" >> "$OUT/qa-summary.txt"
            else
              record_failure "promptQaManagerUi=FAIL expected-system-prompt-controls-missing"
            fi

            if tap_text "$OUT/promptqa-system-prompts-ui.xml" "Core behavior" "promptQaExpandCore"; then
              sleep 1
              capture_screen promptqa-core-expanded
              if dismiss_quickstep_anr promptqa-core-expanded; then
                echo "promptQaCoreExpandedSystemOverlayClear=PASS" >> "$OUT/qa-summary.txt"
              else
                record_failure "promptQaCoreExpandedSystemOverlayClear=FAIL Quickstep ANR persisted"
              fi

              if tap_text "$OUT/promptqa-core-expanded-ui.xml" "Core Turp prompt" "promptQaOpenCoreEditor"; then
                sleep 2
                capture_screen promptqa-core-editor
                if dismiss_quickstep_anr promptqa-core-editor; then
                  echo "promptQaEditorSystemOverlayClear=PASS" >> "$OUT/qa-summary.txt"
                else
                  record_failure "promptQaEditorSystemOverlayClear=FAIL Quickstep ANR persisted"
                fi

                editor_ok=true
                for expected in \
                    "Core Turp prompt" \
                    "Restore built-in" \
                    "Variables" \
                    "Preview" \
                    "Included" \
                    "Save"; do
                  if ! grep -Fq "$expected" "$OUT/promptqa-core-editor-ui.xml" 2>/dev/null; then
                    record_failure "promptQaEditorUi=FAIL missing=${expected}"
                    editor_ok=false
                  fi
                done
                if grep -Fq 'Open system prompts' "$OUT/promptqa-core-editor-ui.xml" 2>/dev/null ||
                   grep -Fq 'Performance counter' "$OUT/promptqa-core-editor-ui.xml" 2>/dev/null; then
                  record_failure "promptQaEditorUi=FAIL settings-page-chrome-leaked-into-editor"
                  editor_ok=false
                fi
                if [[ "$editor_ok" == true ]]; then
                  echo "promptQaEditorUi=PASS" >> "$OUT/qa-summary.txt"
                fi

                editor_title_xy="$(pick_text_center "$OUT/promptqa-core-editor-ui.xml" "Core Turp prompt" 2>/dev/null || true)"
                editor_save_xy="$(pick_text_center "$OUT/promptqa-core-editor-ui.xml" "Save" 2>/dev/null || true)"
                if [[ -n "$editor_title_xy" && -n "$editor_save_xy" ]]; then
                  read -r editor_title_x editor_title_y <<<"$editor_title_xy"
                  read -r editor_save_x editor_save_y <<<"$editor_save_xy"
                  if (( editor_title_y < 450 && editor_save_y < 450 )); then
                    echo "promptQaEditorFullScreen=PASS titleY=${editor_title_y} saveY=${editor_save_y}" >> "$OUT/qa-summary.txt"
                  else
                    record_failure "promptQaEditorFullScreen=FAIL titleY=${editor_title_y} saveY=${editor_save_y}"
                  fi
                else
                  record_failure "promptQaEditorFullScreen=FAIL missing-title-or-save-bounds"
                fi

                variables_xy="$(pick_text_center "$OUT/promptqa-core-editor-ui.xml" "Variables" 2>/dev/null || true)"
                if [[ -n "$variables_xy" ]]; then
                  read -r variables_x variables_y <<<"$variables_xy"
                  adb shell input tap "$variables_x" "$variables_y"
                  echo "promptQaOpenVariables=PASS coord=${variables_x},${variables_y}" >> "$OUT/qa-summary.txt"
                  sleep 1
                  capture_screen promptqa-core-editor-variables
                  if dismiss_quickstep_anr promptqa-core-editor-variables; then
                    echo "promptQaVariablesSystemOverlayClear=PASS" >> "$OUT/qa-summary.txt"
                  else
                    record_failure "promptQaVariablesSystemOverlayClear=FAIL Quickstep ANR persisted"
                  fi
                  if grep -Fq 'Insert variable' "$OUT/promptqa-core-editor-variables-ui.xml" 2>/dev/null; then
                    echo "promptQaVariablesUi=PASS" >> "$OUT/qa-summary.txt"
                  else
                    record_failure "promptQaVariablesUi=FAIL expected-variable-controls-missing"
                  fi
                  [[ -s "$OUT/promptqa-core-editor-variables.png" ]] &&
                    echo "promptQaVariablesScreenshot=PASS" >> "$OUT/qa-summary.txt" ||
                    record_failure "promptQaVariablesScreenshot=FAIL"
                else
                  record_failure "promptQaOpenVariables=FAIL variables-chip-not-found"
                fi

                [[ -s "$OUT/promptqa-core-editor.png" ]] &&
                  echo "promptQaEditorScreenshot=PASS" >> "$OUT/qa-summary.txt" ||
                  record_failure "promptQaEditorScreenshot=FAIL"
              fi
            fi
          else
            record_failure "promptQaOpenSystemPrompts=FAIL row-not-found"
          fi
        else
          record_failure "promptQaOpenDeveloper=FAIL row-not-found"
        fi
      else
        record_failure "promptQaOpenAbout=FAIL row-not-found"
      fi
    fi
  fi
fi

adb shell dumpsys activity top > "$OUT/activity-top.txt" 2>&1 || true
adb shell dumpsys window windows > "$OUT/windows.txt" 2>&1 || true
adb shell dumpsys package "$PACKAGE" > "$OUT/package.txt" 2>&1 || true
adb shell dumpsys meminfo "$PACKAGE" > "$OUT/meminfo.txt" 2>&1 || true
adb shell dumpsys gfxinfo "$PACKAGE" > "$OUT/gfxinfo.txt" 2>&1 || true
adb shell pidof -s "$PACKAGE" > "$OUT/pid.txt" 2>/dev/null || true
adb logcat -b crash -d > "$OUT/logcat-crash.txt" 2>&1 || true
adb logcat -d > "$OUT/logcat.txt" 2>&1 || true

pid="$(tr -d '[:space:]' < "$OUT/pid.txt" 2>/dev/null || true)"
if [[ -n "$pid" ]]; then
  echo "processAlive=PASS pid=$pid" >> "$OUT/qa-summary.txt"
else
  record_failure "processAlive=FAIL"
fi

if grep -Eiq 'FATAL EXCEPTION|Process: app\.turp\.chat\.debug.*has died|ANR in app\.turp\.chat\.debug' "$OUT/logcat.txt" "$OUT/logcat-crash.txt"; then
  record_failure "runtimeCrashOrAnr=FAIL"
else
  echo "runtimeCrashOrAnr=PASS" >> "$OUT/qa-summary.txt"
fi

cat "$OUT/qa-summary.txt"
exit "$status"
