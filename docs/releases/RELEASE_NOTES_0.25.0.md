# Turp 0.25.0

## Providers, rendering, and polish

- Separates a connection's wire protocol from its provider profile, so OpenRouter, OpenCode, and other supported services can keep provider-specific behavior behind arbitrary proxies, mirrors, gateways, or self-hosted base URLs.
- Adds editable per-provider endpoint overrides and custom routing for model catalogs, chat, Responses, Anthropic Messages, Gemini streaming, images, account/usage APIs, and OpenCode server endpoints; presets now act as editable defaults rather than hostname-locked identities.
- Adds first-class OpenCode V2 server support with V2 model discovery and generation while retaining direct OpenCode Go and Zen gateway connections as separate profiles.
- Expands OpenRouter integration with account/key limits and usage, catalog metadata, reasoning support, image-model discovery, and correct USD-per-token to USD-per-million pricing normalization.
- Refines Turp's neutral-surface theme, keeps Arbor as a distinct green identity, and separates their launcher artwork while preserving explicit palette-icon apply/restart behavior.
- Improves model-picker stability for near-full-height Material sheets while preserving native scrolling and gestures.
- Improves rich Markdown presentation, including horizontally scrollable content where needed, and adds developer source inspection for rendered messages.
- Adds database/provider migrations and regression coverage for configurable protocols, provider profiles, endpoint resolution, OpenCode V2, OpenRouter, themes, launcher behavior, model picking, and release metadata.
