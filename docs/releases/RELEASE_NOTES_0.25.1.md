# Turp 0.25.1

## Android provider hotfix

- Fixes request failures on Android caused by an invalid regular expression in provider endpoint-template validation.
- Adds Android instrumentation coverage that executes endpoint expansion on Android's regex engine so this class of runtime-only failure is caught before release.
- Stops streaming and completion haptics when Turp is backgrounded or its window is not focused, while generation continues normally.
- Makes Sudo mode override Turp-authored output-format restrictions, so an explicit Sudo request can print diagnostic/example tool-call JSON or protocol text as inert output even when executable functions are unavailable; Turp still never claims the inert payload executed.
