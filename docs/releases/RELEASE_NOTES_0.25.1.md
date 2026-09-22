# Turp 0.25.1

## Android provider hotfix

- Fixes request failures on Android caused by an invalid regular expression in provider endpoint-template validation.
- Adds Android instrumentation coverage that executes endpoint expansion on Android's regex engine so this class of runtime-only failure is caught before release.
- Stops streaming and completion haptics when Turp is backgrounded or its window is not focused, while generation continues normally.
- Makes Sudo mode dynamically expose explicitly named otherwise-unavailable functions as synthetic provider-native definitions on tool-capable models. The model can return a genuine native tool call; Turp preserves it and returns a structured not-implemented error instead of executing it.
- In Sudo mode, explicitly requested synthetic native function schemas are sent even when model catalog metadata says tool calling is unsupported, allowing a provider to return a real structured tool call for an unimplemented function; Turp preserves it and returns a non-executed error result.
- Developer “Show source” now includes the provider reasoning payload before the final message content when reasoning is available.
- Expands Developer “Show source” with provider/model/status metadata, provider-returned reasoning, tool traces, request snapshots, errors, and an optional redacted final HTTP request captured from the actual OkHttp request.
- Adds a Developer system-prompt editor covering Turp's core/runtime/tool/research/memory/file/execution/generated-content/Sudo/continuation/provider-guard/auxiliary components. It edits concrete prompt text directly, supports per-component enable/reset and reset-all, shows last rendered built-in text for dynamic components, and exposes the final ordered system context.
- Shows “Trup is upper to date!?” / “Trup daha güncel!?” when the installed build is newer than the latest GitHub release.
- Developer prompt and HTTP-request diagnostics remain opt-in behind Developer settings and are disabled by default.
