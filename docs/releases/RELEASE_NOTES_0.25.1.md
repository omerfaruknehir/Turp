# Turp 0.25.1

## Android provider hotfix

- Fixes request failures on Android caused by an invalid regular expression in provider endpoint-template validation.
- Adds Android instrumentation coverage that executes endpoint expansion on Android's regex engine so this class of runtime-only failure is caught before release.
- Stops streaming and completion haptics when Turp is backgrounded or its window is not focused, while generation continues normally.
- Makes Sudo mode dynamically expose explicitly named otherwise-unavailable functions as synthetic provider-native definitions on tool-capable models. The model can return a genuine native tool call; Turp preserves it and returns a structured not-implemented error instead of executing it.
- In Sudo mode, explicitly requested synthetic native function schemas are sent even when model catalog metadata says tool calling is unsupported, allowing a provider to return a real structured tool call for an unimplemented function; Turp preserves it and returns a non-executed error result.
- Adds normal (non-Sudo) fallback tool calling for models/endpoints that cannot use provider-native function schemas. Native function calling remains preferred; fallback can be enabled globally under Providers & Models or overridden per model, uses a strict whole-response tool envelope, automatically takes over when enabled and a provider rejects native schemas, and offers one-tap enable-and-retry from unsupported-tool and runtime-rejection warnings.
- Developer “Show source” now includes the provider reasoning payload before the final message content when reasoning is available.
- Expands Developer “Show source” with provider/model/status metadata, provider-returned reasoning, tool traces, request snapshots, errors, and an optional redacted final HTTP request captured from the actual OkHttp request.
- Adds a Developer system-context inspector covering Turp's core/runtime/tool/research/memory/file/execution/generated-content/Sudo/continuation/provider-guard/auxiliary prompt sources. The System Context inspector separates the exact ordered provider-boundary system messages from family-based prompt customization; tapping a prompt opens a dedicated full-screen editor, while low-level wrappers and retry plumbing live under Advanced internals. Prompt templates are directly editable, saved edits apply immediately, optional layers can be excluded without losing their text, runtime variables such as `{{app_version}}` and `{{sudo_user_instruction}}` are supported, and exact sent context remains inspectable.
- Shows “Trup is upper to date!?” / “Trup daha güncel!?” when the installed build is newer than the latest GitHub release.
- Developer prompt and HTTP-request diagnostics remain opt-in behind Developer settings and are disabled by default.
