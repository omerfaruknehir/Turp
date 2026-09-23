package app.turp.chat.ui

import app.turp.chat.R

enum class SettingsRoute(
    val titleRes: Int,
) {
    HOME(R.string.settings_title),
    DEFAULTS(R.string.settings_new_chat_defaults),
    RESPONSE_STYLE(R.string.settings_response_style),
    SEARCH(R.string.settings_search_web),
    AUTOMATION(R.string.settings_background_tasks),
    MEMORY(R.string.settings_memory),
    LANGUAGE(R.string.language_dialog_title),
    APPEARANCE(R.string.settings_appearance),
    PRIVACY(R.string.settings_privacy_safety),
    BACKUP(R.string.settings_backup_transfer),
    LOCAL_EXECUTION(R.string.settings_local_execution),
    DEVELOPER(R.string.settings_developer),
    PROMPT_EDITOR(R.string.settings_prompt_editor),
    SYSTEM_PROMPTS(R.string.settings_custom_instructions),
    PROVIDERS(R.string.settings_providers_models),
    ABOUT(R.string.settings_about_turp),
    LICENSES(R.string.settings_licenses_notices),
    ;
}
