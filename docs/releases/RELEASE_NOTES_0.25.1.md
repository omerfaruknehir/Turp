# Turp 0.25.1

## Android provider hotfix

- Fixes request failures on Android caused by an invalid regular expression in provider endpoint-template validation.
- Adds Android instrumentation coverage that executes endpoint expansion on Android's regex engine so this class of runtime-only failure is caught before release.
