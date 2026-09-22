package app.turp.chat.ui

import app.turp.chat.R

/** Resource bridge for legacy String call sites. Static translations live in Android resources. */
internal fun turpUiStringResource(text: String): Int? = when (text) {
    "0% disables blur. Higher values increase the panel-local blur radius." -> R.string.ui_copy_0_disables_blur_higher_values_increase_the_fa65bb87
    "0% is transparent. 100% is a fully opaque panel tint." -> R.string.ui_copy_0_is_transparent_100_is_a_fully_opaque_pan_f33fa680
    "100% is fully opaque and hides background blur." -> R.string.ui_copy_100_is_fully_opaque_and_hides_background_b_1ed78039
    "1–600 seconds. Pure Python is interrupted at the deadline; a blocking native extension may return later." -> R.string.ui_copy_1_600_seconds_pure_python_is_interrupted_a_68f99b1b
    "3–20 results per search call" -> R.string.ui_copy_3_20_results_per_search_call_384a6497
    "A ChatGPT usage limit has been reached." -> R.string.ui_copy_a_chatgpt_usage_limit_has_been_reached_77e6f623
    "A file referenced by this answer." -> R.string.ui_copy_a_file_referenced_by_this_answer_b8ddc412
    "A pair is one request plus its answer. Working history has its own budget inside the total context ceiling." -> R.string.ui_copy_a_pair_is_one_request_plus_its_answer_work_6e74e5ec
    "A provider is needed only when you send a message. You may connect one now or continue and do it later." -> R.string.ui_copy_a_provider_is_needed_only_when_you_send_a__fda9803f
    "A separately selected model allows or denies the preflight plan" -> R.string.ui_copy_a_separately_selected_model_allows_or_deni_e473e04c
    "A source used to support the surrounding claim." -> R.string.ui_copy_a_source_used_to_support_the_surrounding_c_e0c44159
    "AMOLED black" -> R.string.ui_copy_amoled_black_39b130d7
    "AMOLED black only changes dark mode surfaces." -> R.string.ui_copy_amoled_black_only_changes_dark_mode_surfac_378525c5
    "API base URL" -> R.string.ui_copy_api_base_url_1358fba4
    "API key" -> R.string.ui_copy_api_key_cf678cab
    "API key (optional)" -> R.string.ui_copy_api_key_optional_ad9807cb
    "API key is required" -> R.string.ui_copy_api_key_is_required_f36e208b
    "API key missing" -> R.string.ui_copy_api_key_missing_55dd8fbe
    "API key saved securely" -> R.string.ui_copy_api_key_saved_securely_279b6719
    "API model ID" -> R.string.ui_copy_api_model_id_d4e4b444
    "ARCHIVED CHATS" -> R.string.ui_copy_archived_chats_f8b0180b
    "About" -> R.string.ui_copy_about_6b21fb79
    "About Turp" -> R.string.ui_copy_about_turp_be7ca9fe
    "Access key ID" -> R.string.ui_copy_access_key_id_2ea1eee1
    "Account connected" -> R.string.ui_copy_account_connected_4d04eece
    "Add" -> R.string.ui_copy_add_61cc55aa
    "Add API" -> R.string.ui_copy_add_api_0968d619
    "Add ChatGPT" -> R.string.ui_copy_add_chatgpt_6b5fbad1
    "Add ChatGPT provider" -> R.string.ui_copy_add_chatgpt_provider_857271ac
    "Add a ChatGPT account or configure an API-compatible provider." -> R.string.ui_copy_add_a_chatgpt_account_or_configure_an_api__fd220cdb
    "Add a usable provider in the Providers tab." -> R.string.ui_copy_add_a_usable_provider_in_the_providers_tab_71a7c134
    "Add an image, then describe the edit…" -> R.string.ui_copy_add_an_image_then_describe_the_edit_28fdc0b6
    "Add another" -> R.string.ui_copy_add_another_e9e7205b
    "Add at least one reference image." -> R.string.ui_copy_add_at_least_one_reference_image_a2601038
    "Add favorite" -> R.string.ui_copy_add_favorite_4914d44e
    "Add image" -> R.string.ui_copy_add_image_5375634e
    "Add memory" -> R.string.ui_copy_add_memory_095fb8b0
    "Add model" -> R.string.ui_copy_add_model_b6a86ca1
    "Add provider" -> R.string.ui_copy_add_provider_51922162
    "Add reference image" -> R.string.ui_copy_add_reference_image_51ffa371
    "Add to chat" -> R.string.ui_copy_add_to_chat_44d6fb37
    "Add your first API provider" -> R.string.ui_copy_add_your_first_api_provider_99b89dbf
    "Additional credits available" -> R.string.ui_copy_additional_credits_available_f088a3d0
    "Additional instructions" -> R.string.ui_copy_additional_instructions_6ad18129
    "Advanced" -> R.string.ui_copy_advanced_4d064726
    "Advanced compatibility" -> R.string.ui_copy_advanced_compatibility_34b18d92
    "Advanced headers" -> R.string.ui_copy_advanced_headers_6939bef8
    "Advanced package sources" -> R.string.ui_copy_advanced_package_sources_ced8149e
    "All" -> R.string.ui_copy_all_6a720856
    "All categories" -> R.string.ui_copy_all_categories_060be00f
    "All chats" -> R.string.ui_copy_all_chats_398e5b9e
    "All providers" -> R.string.ui_copy_all_providers_352b5847
    "Allocation / blocking GC pressure" -> R.string.ui_copy_allocation_blocking_gc_pressure_981e4d64
    "Allow" -> R.string.ui_copy_allow_3ad0e369
    "Allow and install" -> R.string.ui_copy_allow_and_install_54bbf8d7
    "Allow models to save stable, non-sensitive details; duplicate items are merged" -> R.string.ui_copy_allow_models_to_save_stable_non_sensitive__56f7f457
    "Allow package changes?" -> R.string.ui_copy_allow_package_changes_db149919
    "Allow package installation?" -> R.string.ui_copy_allow_package_installation_f80b8c4b
    "Allow page fetching" -> R.string.ui_copy_allow_page_fetching_d42ed0a5
    "Allow pip direct references and relaxed apt names; command-line options remain blocked" -> R.string.ui_copy_allow_pip_direct_references_and_relaxed_ap_bfca3617
    "Amazon S3, MinIO, Backblaze B2, or another compatible bucket" -> R.string.ui_copy_amazon_s3_minio_backblaze_b2_or_another_co_7a850576
    "An external page linked from this answer." -> R.string.ui_copy_an_external_page_linked_from_this_answer_c8d5d675
    "Android grants Turp persistent access only to the folder you select. Create or choose a dedicated Turp folder; no account-wide permission is requested." -> R.string.ui_copy_android_grants_turp_persistent_access_on_34c55edf
    "Android themed icons can recolor Turp's monochrome layer. Dynamic uses the live wallpaper-derived Material You palette when themed icons are off." -> R.string.ui_copy_android_themed_icons_can_recolor_turp_s__f7184987
    "App settings and configuration" -> R.string.ui_copy_app_settings_and_configuration_54efc74b
    "Appearance" -> R.string.ui_copy_appearance_41def7a0
    "Appearance, backups, memory, and advanced behavior are grouped for later." -> R.string.ui_copy_appearance_backups_memory_and_advanced_beh_5e5344ce
    "Apply" -> R.string.ui_copy_apply_cfea419c
    "Approval model" -> R.string.ui_copy_approval_model_b981e10d
    "Approval required" -> R.string.ui_copy_approval_required_b8d012c2
    "Archive" -> R.string.ui_copy_archive_2621c6fd
    "Archive password" -> R.string.ui_copy_archive_password_afd11d6f
    "Archived" -> R.string.ui_copy_archived_eddc813f
    "Archived chats will appear here." -> R.string.ui_copy_archived_chats_will_appear_here_c94c149a
    "Ask AI to retry" -> R.string.ui_copy_ask_ai_to_retry_0f716158
    "Ask every time" -> R.string.ui_copy_ask_every_time_c9296e65
    "Assistant responses" -> R.string.ui_copy_assistant_responses_5c707a00
    "Attachments are unavailable in image generation mode" -> R.string.ui_copy_attachments_are_unavailable_in_image_gener_9c4dc302
    "Auto-approve" -> R.string.ui_copy_auto_approve_b9ab9fe7
    "Auto-approve only package names you list" -> R.string.ui_copy_auto_approve_only_package_names_you_list_c5d8eddd
    "Automatic" -> R.string.ui_copy_automatic_ac9041e7
    "Automatic memory" -> R.string.ui_copy_automatic_memory_d8913794
    "Automatic repair attempts" -> R.string.ui_copy_automatic_repair_attempts_c33ea5b8
    "Automatic repair unavailable" -> R.string.ui_copy_automatic_repair_unavailable_de47b795
    "Availability in new chats" -> R.string.ui_copy_availability_in_new_chats_8ce2f36c
    "Available levels follow the selected model. Some models cannot fully disable reasoning." -> R.string.ui_copy_available_levels_follow_the_selected_model_66af3032
    "Avoid decorative emoji and use them only when they add meaning" -> R.string.ui_copy_avoid_decorative_emoji_and_use_them_only_w_e772bb3f
    "Back" -> R.string.ui_copy_back_b52b36b7
    "Back to Settings" -> R.string.ui_copy_back_to_settings_a8c1255a
    "Back to runtime manager" -> R.string.ui_copy_back_to_runtime_manager_c1fe861f
    "Back to setup" -> R.string.ui_copy_back_to_setup_97e95eff
    "Back up now" -> R.string.ui_copy_back_up_now_527bf1aa
    "Background task models" -> R.string.ui_copy_background_task_models_ff23eed1
    "Background tasks" -> R.string.ui_copy_background_tasks_3a783601
    "Backup" -> R.string.ui_copy_backup_dd96994d
    "Backup & transfer" -> R.string.ui_copy_backup_transfer_84bb295b
    "Backup downloaded. Opening preview…" -> R.string.ui_copy_backup_downloaded_opening_preview_b365dfae
    "Backup failed" -> R.string.ui_copy_backup_failed_7e1a41f8
    "Backup restored. Setup was paused; finish provider access later from Settings." -> R.string.ui_copy_backup_restored_setup_was_paused_finish_pr_5b8e9941
    "Backup saved" -> R.string.ui_copy_backup_saved_66cbc149
    "Backups" -> R.string.ui_copy_backups_530cc25b
    "Base URL" -> R.string.ui_copy_base_url_1dbd61f5
    "Before the first install" -> R.string.ui_copy_before_the_first_install_450436fa
    "Blur" -> R.string.ui_copy_blur_900aa998
    "Blur boundary diagnostics" -> R.string.ui_copy_blur_boundary_diagnostics_0cdf5b67
    "Bottom left" -> R.string.ui_copy_bottom_left_1a7ce96f
    "Bottom right" -> R.string.ui_copy_bottom_right_c2fc5768
    "Bring your own models" -> R.string.ui_copy_bring_your_own_models_b312a997
    "Bucket" -> R.string.ui_copy_bucket_40dafe4c
    "Buffer swap" -> R.string.ui_copy_buffer_swap_b7717fd1
    "Bugs, regressions, and feature requests" -> R.string.ui_copy_bugs_regressions_and_feature_requests_bdbada43
    "Build" -> R.string.ui_copy_build_bbd80cf7
    "Build information" -> R.string.ui_copy_build_information_ddc79597
    "Build source" -> R.string.ui_copy_build_source_27f4fe69
    "Bundled Python 3.12 · no Linux download required" -> R.string.ui_copy_bundled_python_3_12_no_linux_download_requ_9b30e286
    "Bundled runtime · loading this chat's environment…" -> R.string.ui_copy_bundled_runtime_loading_this_chat_s_enviro_2d613676
    "Bundled suggestions" -> R.string.ui_copy_bundled_suggestions_b7b1ca8f
    "Cached input" -> R.string.ui_copy_cached_input_36e191c9
    "Cancel" -> R.string.ui_copy_cancel_77dfd213
    "Category" -> R.string.ui_copy_category_a3c686e7
    "Cause profiler" -> R.string.ui_copy_cause_profiler_5b1920f7
    "Change folder" -> R.string.ui_copy_change_folder_2de7d622
    "Changed files" -> R.string.ui_copy_changed_files_fe25abc0
    "Chat" -> R.string.ui_copy_chat_2ced57f1
    "Chat behavior" -> R.string.ui_copy_chat_behavior_bc3ca2c7
    "Chat naming" -> R.string.ui_copy_chat_naming_1451295a
    "Chat naming and context compression models" -> R.string.ui_copy_chat_naming_and_context_compression_models_33617241
    "ChatGPT OAuth • Connected" -> R.string.ui_copy_chatgpt_oauth_connected_cddd12b6
    "ChatGPT OAuth • Disconnected" -> R.string.ui_copy_chatgpt_oauth_disconnected_542cca40
    "ChatGPT OAuth • Needs attention" -> R.string.ui_copy_chatgpt_oauth_needs_attention_b6ddfac0
    "ChatGPT OAuth • Signing in" -> R.string.ui_copy_chatgpt_oauth_signing_in_9a70e7b4
    "Chats are kept and moved back to All chats." -> R.string.ui_copy_chats_are_kept_and_moved_back_to_all_chats_2d13eaa4
    "Chats, credentials, and tool workspaces stay on this device." -> R.string.ui_copy_chats_credentials_and_tool_workspaces_stay_38633b64
    "Chats, credentials, and workspaces stay on your device. Turp connects directly to providers you configure and has no application backend, ads, or telemetry." -> R.string.ui_copy_chats_credentials_and_workspaces_stay_on_y_1ee4fc0c
    "Check again" -> R.string.ui_copy_check_again_e185048c
    "Check automatically" -> R.string.ui_copy_check_automatically_7cd229d2
    "Check for updates" -> R.string.ui_copy_check_for_updates_736b9062
    "Check the source repository once per day when Turp starts" -> R.string.ui_copy_check_the_source_repository_once_per_day_w_7fdea90f
    "Checking…" -> R.string.ui_copy_checking_820d6004
    "Choose a model" -> R.string.ui_copy_choose_a_model_fefb3326
    "Choose a provider, then manage its connection and models." -> R.string.ui_copy_choose_a_provider_then_manage_its_connecti_b661d571
    "Choose a restrained built-in palette or Android dynamic colors. Every swatch is rendered from that palette, not the currently selected one." -> R.string.ui_copy_choose_a_restrained_built_in_palette_or_an_9629ebc4
    "Choose approval model" -> R.string.ui_copy_choose_approval_model_411af022
    "Choose automation model" -> R.string.ui_copy_choose_automation_model_91c89c6f
    "Choose folder" -> R.string.ui_copy_choose_folder_1838a415
    "Choose how Turp names chats and compresses older context." -> R.string.ui_copy_choose_how_turp_names_chats_and_compress_0c947c25
    "Choose image model" -> R.string.ui_copy_choose_image_model_fc20b463
    "Choose when Turp may install Python or Linux packages and which sources are trusted." -> R.string.ui_copy_choose_when_turp_may_install_python_or_l_0e6027fb
    "Clear" -> R.string.ui_copy_clear_719ea396
    "Clear terminal" -> R.string.ui_copy_clear_terminal_b754d96c
    "Close" -> R.string.ui_copy_close_bbfa773e
    "Cloud backup" -> R.string.ui_copy_cloud_backup_b92fde93
    "Cloud backups, local archives, and restore" -> R.string.ui_copy_cloud_backups_local_archives_and_restore_96cd6161
    "Collapsed" -> R.string.ui_copy_collapsed_0084e8fa
    "Color scheme" -> R.string.ui_copy_color_scheme_9407f901
    "Comma, space, or newline separated" -> R.string.ui_copy_comma_space_or_newline_separated_863eb052
    "Command" -> R.string.ui_copy_command_8901895f
    "Commands run as uid 0 inside the selected PRoot distribution." -> R.string.ui_copy_commands_run_as_uid_0_inside_the_selected__e5bf6bf8
    "Complete sign-in in your browser…" -> R.string.ui_copy_complete_sign_in_in_your_browser_c7362bbd
    "Completed" -> R.string.ui_copy_completed_1798b3ba
    "Composer defaults" -> R.string.ui_copy_composer_defaults_f24a4c75
    "Configure a usable provider to enable model-based automation." -> R.string.ui_copy_configure_a_usable_provider_to_enable_mode_e379478d
    "Configured in USD per million tokens" -> R.string.ui_copy_configured_in_usd_per_million_tokens_a713ef80
    "Confirm password" -> R.string.ui_copy_confirm_password_4a7c565d
    "Connect & fetch models" -> R.string.ui_copy_connect_fetch_models_50cf8512
    "Connect Google Drive" -> R.string.ui_copy_connect_google_drive_4a12c0a3
    "Connect a model provider" -> R.string.ui_copy_connect_a_model_provider_3447ed6f
    "Connect a provider" -> R.string.ui_copy_connect_a_provider_6967f288
    "Connect account" -> R.string.ui_copy_connect_account_8ff19dc8
    "Connect folder" -> R.string.ui_copy_connect_folder_a423804a
    "Connect to Ollama, llama.cpp, or LM Studio." -> R.string.ui_copy_connect_to_ollama_llama_cpp_or_lm_studio_ac14c5fe
    "Connected" -> R.string.ui_copy_connected_c2f9b7b4
    "Connecting…" -> R.string.ui_copy_connecting_fd3e7969
    "Connection details stay out of the way until you need them." -> R.string.ui_copy_connection_details_stay_out_of_the_way_unt_1060adf3
    "Connection failed • tap to retry" -> R.string.ui_copy_connection_failed_tap_to_retry_58696557
    "Context" -> R.string.ui_copy_context_cc11b3a2
    "Context & output" -> R.string.ui_copy_context_output_1a6d3860
    "Context compression" -> R.string.ui_copy_context_compression_28788cf9
    "Context token ceiling" -> R.string.ui_copy_context_token_ceiling_06a47c81
    "Context tokens" -> R.string.ui_copy_context_tokens_4d6993db
    "Continue" -> R.string.ui_copy_continue_2e026239
    "Continue without one" -> R.string.ui_copy_continue_without_one_f9f05eb4
    "Controls how Turp handles AI-generated interactive UI." -> R.string.ui_copy_controls_how_turp_handles_ai_generated_i_48acf2ef
    "Controls whether reasoning and tool cards expand automatically; they remain saved either way." -> R.string.ui_copy_controls_whether_reasoning_and_tool_cards__21e3df21
    "Controls whether this custom endpoint uses chat/completions or images/generations." -> R.string.ui_copy_controls_whether_this_custom_endpoint_uses_284b17b7
    "Conversation deleted" -> R.string.ui_copy_conversation_deleted_798eb3c2
    "Cool teal and cyan accents" -> R.string.ui_copy_cool_teal_and_cyan_accents_62328729
    "Copied" -> R.string.ui_copy_copied_8e3df45a
    "Copy" -> R.string.ui_copy_copy_af74f7c5
    "Copy errors" -> R.string.ui_copy_copy_errors_404ef8c8
    "Copy output" -> R.string.ui_copy_copy_output_944a2cd1
    "Copy setup details" -> R.string.ui_copy_copy_setup_details_aa78ee1e
    "Copy source" -> R.string.ui_copy_copy_source_3dd78600
    "Cost" -> R.string.ui_copy_cost_64ae43e8
    "Cost unavailable" -> R.string.ui_copy_cost_unavailable_194b13a4
    "Could not connect the cloud folder" -> R.string.ui_copy_could_not_connect_the_cloud_folder_1504bc6a
    "Could not create the chat file" -> R.string.ui_copy_could_not_create_the_chat_file_6e2e9b13
    "Could not delete backup" -> R.string.ui_copy_could_not_delete_backup_e58b4033
    "Could not download and inspect the cloud backup" -> R.string.ui_copy_could_not_download_and_inspect_the_cloud_b_371f41c9
    "Could not download backup" -> R.string.ui_copy_could_not_download_backup_b7894d83
    "Could not fetch the model list" -> R.string.ui_copy_could_not_fetch_the_model_list_9f107d0a
    "Could not inspect archive" -> R.string.ui_copy_could_not_inspect_archive_ba34f9f2
    "Could not list backups" -> R.string.ui_copy_could_not_list_backups_406f0469
    "Could not open backup" -> R.string.ui_copy_could_not_open_backup_7cee936c
    "Could not open cloud sign-in" -> R.string.ui_copy_could_not_open_cloud_sign_in_b6cc6d9e
    "Could not open the update link" -> R.string.ui_copy_could_not_open_the_update_link_954b53f6
    "Could not read Google Drive app storage" -> R.string.ui_copy_could_not_read_google_drive_app_storage_c11ea250
    "Could not read the cloud folder" -> R.string.ui_copy_could_not_read_the_cloud_folder_cfea9b4c
    "Could not read the connected cloud folder" -> R.string.ui_copy_could_not_read_the_connected_cloud_folder_086bff90
    "Could not read the selected cloud folder" -> R.string.ui_copy_could_not_read_the_selected_cloud_folder_02907068
    "Could not unlock archive" -> R.string.ui_copy_could_not_unlock_archive_41801e70
    "Create" -> R.string.ui_copy_create_6e157c5d
    "Create image" -> R.string.ui_copy_create_image_8ea312d6
    "Create new project" -> R.string.ui_copy_create_new_project_dcd5a8d5
    "Created by @omerfaruknehir" -> R.string.ui_copy_created_by_omerfaruknehir_f66bf993
    "Creating and uploading…" -> R.string.ui_copy_creating_and_uploading_6059a4cb
    "Credentials are encrypted with Android Keystore and are never included in Turp backups." -> R.string.ui_copy_credentials_are_encrypted_with_android_key_8b5ed846
    "Credentials are encrypted with Android Keystore and sent only to the provider you choose." -> R.string.ui_copy_credentials_are_encrypted_with_android_key_51e96499
    "Credentials were found and this step is complete." -> R.string.ui_copy_credentials_were_found_and_this_step_is_co_ae35eccc
    "Credits: unlimited" -> R.string.ui_copy_credits_unlimited_a3b91acc
    "Cross-chat facts and preferences stored locally" -> R.string.ui_copy_cross_chat_facts_and_preferences_stored_lo_c9095e5c
    "Current account quota windows" -> R.string.ui_copy_current_account_quota_windows_a4789434
    "Custom headers JSON" -> R.string.ui_copy_custom_headers_json_09006e39
    "Custom instruction profiles" -> R.string.ui_copy_custom_instruction_profiles_026ede6b
    "Custom instructions" -> R.string.ui_copy_custom_instructions_fd7e5206
    "Custom provider" -> R.string.ui_copy_custom_provider_fd020661
    "Custom system prompt" -> R.string.ui_copy_custom_system_prompt_c6dd0cc6
    "DIAGRAM • native" -> R.string.ui_copy_diagram_native_598faf2d
    "Dark" -> R.string.ui_copy_dark_ae1ef014
    "Data deletion" -> R.string.ui_copy_data_deletion_a19c4cb5
    "Deep Research" -> R.string.ui_copy_deep_research_8bcb0868
    "Deep Research plans, searches iteratively, verifies sources, and produces a cited report. Enabling it also enables web search." -> R.string.ui_copy_deep_research_plans_searches_iteratively_v_87244f15
    "Deep purple with soft rose accents" -> R.string.ui_copy_deep_purple_with_soft_rose_accents_5389832c
    "Default" -> R.string.ui_copy_default_808d7dca
    "Delete" -> R.string.ui_copy_delete_f6fdbe48
    "Delete backup" -> R.string.ui_copy_delete_backup_4fcb0270
    "Delete cloud backup?" -> R.string.ui_copy_delete_cloud_backup_55d4064d
    "Delete disabled" -> R.string.ui_copy_delete_disabled_fcc7cb3b
    "Delete local data and provider-held copies" -> R.string.ui_copy_delete_local_data_and_provider_held_copies_ea5dd121
    "Delete memory" -> R.string.ui_copy_delete_memory_bcef2182
    "Delete memory?" -> R.string.ui_copy_delete_memory_c84844d4
    "Delete permanently" -> R.string.ui_copy_delete_permanently_e63b5153
    "Delete project" -> R.string.ui_copy_delete_project_af1456f2
    "Deleted system prompt" -> R.string.ui_copy_deleted_system_prompt_06c8a0bb
    "Deny" -> R.string.ui_copy_deny_53577bb5
    "Dependencies" -> R.string.ui_copy_dependencies_0562f32d
    "Dependency" -> R.string.ui_copy_dependency_ce311abe
    "Describe an image to generate…" -> R.string.ui_copy_describe_an_image_to_generate_6cf98862
    "Describe an image…" -> R.string.ui_copy_describe_an_image_4f20cc05
    "Describe the changes…" -> R.string.ui_copy_describe_the_changes_cbc25316
    "Describe the image you want to create." -> R.string.ui_copy_describe_the_image_you_want_to_create_adbeecb1
    "Describe the image you want. This model generates new images without reference-image editing." -> R.string.ui_copy_describe_the_image_you_want_this_model_gen_c188561d
    "Describe the image you want…" -> R.string.ui_copy_describe_the_image_you_want_1d608b3f
    "Detailed metrics" -> R.string.ui_copy_detailed_metrics_651ab820
    "Details" -> R.string.ui_copy_details_dc3decbb
    "Developer options" -> R.string.ui_copy_developer_options_7ea27f6f
    "Developer options · enabled" -> R.string.ui_copy_developer_options_enabled_1ce8c70e
    "Developer settings" -> R.string.ui_copy_developer_settings_10c12c96
    "Development disclosure & disclaimer" -> R.string.ui_copy_development_disclosure_disclaimer_51bf9f99
    "Device ABI" -> R.string.ui_copy_device_abi_08110147
    "Diagram renderer found no supported nodes" -> R.string.ui_copy_diagram_renderer_found_no_supported_nodes_13e8fcbc
    "Direct HTTPS WebDAV support for Nextcloud, ownCloud, NAS servers, and compatible hosts. Use an app password when available." -> R.string.ui_copy_direct_https_webdav_support_for_nextcloud__7ef97b4e
    "Direct app storage" -> R.string.ui_copy_direct_app_storage_566d9e69
    "Disable" -> R.string.ui_copy_disable_9a7d4e06
    "Disable all" -> R.string.ui_copy_disable_all_485c590b
    "Disable only for a trusted local or keyless endpoint" -> R.string.ui_copy_disable_only_for_a_trusted_local_or_keyles_aa836bd5
    "Disable only for your own local/keyless endpoint" -> R.string.ui_copy_disable_only_for_your_own_local_keyless_en_79ff86af
    "Disabled" -> R.string.ui_copy_disabled_f4f4473d
    "Disabled memories are currently excluded from chats. This cleanup permanently removes them." -> R.string.ui_copy_disabled_memories_are_currently_excluded_f_beba57c8
    "Disconnect" -> R.string.ui_copy_disconnect_ed28e068
    "Disconnect account" -> R.string.ui_copy_disconnect_account_37546bdc
    "Disconnected" -> R.string.ui_copy_disconnected_771e05f2
    "Dismiss error" -> R.string.ui_copy_dismiss_error_347aaa77
    "Display name" -> R.string.ui_copy_display_name_c7874aaa
    "Display-only Markdown cannot edit generated content" -> R.string.ui_copy_display_only_markdown_cannot_edit_generate_d95a9581
    "Display-only Markdown cannot execute Linux tools" -> R.string.ui_copy_display_only_markdown_cannot_execute_linux_7846f40e
    "Display-only Markdown cannot execute Python" -> R.string.ui_copy_display_only_markdown_cannot_execute_pytho_17174215
    "Display-only Markdown cannot install packages" -> R.string.ui_copy_display_only_markdown_cannot_install_packa_8c322f5c
    "Display-only Markdown cannot repair generated content" -> R.string.ui_copy_display_only_markdown_cannot_repair_genera_beeef88e
    "Display-only Markdown cannot review packages" -> R.string.ui_copy_display_only_markdown_cannot_review_packag_cbb98ac1
    "Display-only Markdown cannot run widgets" -> R.string.ui_copy_display_only_markdown_cannot_run_widgets_54d21ddb
    "Distribution selection, installation, packages, and removal are managed in one place." -> R.string.ui_copy_distribution_selection_installation_packag_e2045a42
    "Documents, archives, code, audio, and other supported files" -> R.string.ui_copy_documents_archives_code_audio_and_other_su_c14d4dd9
    "Done" -> R.string.ui_copy_done_e9b450d1
    "Double-tap, pinch, or drag" -> R.string.ui_copy_double_tap_pinch_or_drag_3802518b
    "Download" -> R.string.ui_copy_download_a479c9c3
    "Download image" -> R.string.ui_copy_download_image_d6f34a97
    "Download update" -> R.string.ui_copy_download_update_870d57b0
    "Draws explicit debug guides at the top and bottom panel boundaries. Normal UI no longer draws a boundary highlight." -> R.string.ui_copy_draws_explicit_debug_guides_at_the_top_and_5be7ef59
    "Dropbox" -> R.string.ui_copy_dropbox_ac88abd7
    "Dropbox Turp App folder" -> R.string.ui_copy_dropbox_turp_app_folder_eb60f8b3
    "Duplicate content recording for blur" -> R.string.ui_copy_duplicate_content_recording_for_blur_abb2e364
    "Dynamic" -> R.string.ui_copy_dynamic_ceb739df
    "Each chat has a persistent bundled-Python session and isolated .packages directory. It works without installing Linux and remains confined by Android's app sandbox." -> R.string.ui_copy_each_chat_has_a_persistent_bundled_python__0645477c
    "Each provider keeps its OAuth session, models, usage limits, and refresh state separate. Turp requests a fresh sign-in page so you can add a different ChatGPT account." -> R.string.ui_copy_each_provider_keeps_its_oauth_session_mode_92069f27
    "Edge softness" -> R.string.ui_copy_edge_softness_535764f7
    "Edit" -> R.string.ui_copy_edit_5301648d
    "Edit connection" -> R.string.ui_copy_edit_connection_b3171b22
    "Edit custom profile" -> R.string.ui_copy_edit_custom_profile_731c56b7
    "Edit image" -> R.string.ui_copy_edit_image_2b4c1f49
    "Edit images" -> R.string.ui_copy_edit_images_b368ef68
    "Edit memory" -> R.string.ui_copy_edit_memory_67cbb906
    "Edit message" -> R.string.ui_copy_edit_message_96116a52
    "Edit model" -> R.string.ui_copy_edit_model_3e5f6c0b
    "Edit source" -> R.string.ui_copy_edit_source_93da9166
    "Editing image…" -> R.string.ui_copy_editing_image_7b5e4124
    "Emoji use and global answer presentation" -> R.string.ui_copy_emoji_use_and_global_answer_presentation_0d034f89
    "Enable" -> R.string.ui_copy_enable_20063ad9
    "Enable all" -> R.string.ui_copy_enable_all_1734db95
    "Enable developer settings" -> R.string.ui_copy_enable_developer_settings_73835d35
    "Enabled" -> R.string.ui_copy_enabled_df174a3f
    "Enabled by default. Technical symbols and emoji requested by the user are not blocked." -> R.string.ui_copy_enabled_by_default_technical_symbols_and_e_053fa2a9
    "Encryption password (optional)" -> R.string.ui_copy_encryption_password_optional_d7003844
    "Endpoint" -> R.string.ui_copy_endpoint_92ec6350
    "Enter Turp" -> R.string.ui_copy_enter_turp_7e429cf0
    "Enter Turp now. You can return to Providers & models from Settings at any time." -> R.string.ui_copy_enter_turp_now_you_can_return_to_provide_a37af7f9
    "Enter a server folder and credentials" -> R.string.ui_copy_enter_a_server_folder_and_credentials_ac04ef74
    "Enter the exact HTTPS URL of a dedicated Turp folder. For Nextcloud this normally ends with /remote.php/dav/files/USERNAME/Turp/." -> R.string.ui_copy_enter_the_exact_https_url_of_a_dedicated_x_91f38cfa
    "Everything listed here is embedded in this build and available without a network connection." -> R.string.ui_copy_everything_listed_here_is_embedded_in_this_6cdf576d
    "Everything requested is already installed." -> R.string.ui_copy_everything_requested_is_already_installed_65a85f67
    "Exact provider counters where available, then local family and generic fallbacks." -> R.string.ui_copy_exact_provider_counters_where_available_th_0741822f
    "Execution completed" -> R.string.ui_copy_execution_completed_0fd90c4b
    "Execution deadline (seconds)" -> R.string.ui_copy_execution_deadline_seconds_6b512627
    "Execution exceeded its configured deadline." -> R.string.ui_copy_execution_exceeded_its_configured_deadline_53cf3584
    "Execution failed" -> R.string.ui_copy_execution_failed_cd20d9a5
    "Expand step" -> R.string.ui_copy_expand_step_589a7b3c
    "Expand work details" -> R.string.ui_copy_expand_work_details_49048b51
    "Expanded" -> R.string.ui_copy_expanded_6d170474
    "Export" -> R.string.ui_copy_export_f3e4fadb
    "Export backup" -> R.string.ui_copy_export_backup_6043b4bd
    "Export to file" -> R.string.ui_copy_export_to_file_b277e65c
    "Expose selected enabled memories to chats and allow memory tools" -> R.string.ui_copy_expose_selected_enabled_memories_to_chats__75fb8ea4
    "Expose web_fetch so the model can read public HTTPS pages after searching." -> R.string.ui_copy_expose_web_fetch_so_the_model_can_read_pub_9e8b0c30
    "Extended reasoning" -> R.string.ui_copy_extended_reasoning_9ce457a6
    "External link" -> R.string.ui_copy_external_link_66305476
    "Extra high" -> R.string.ui_copy_extra_high_e8d45012
    "Failed" -> R.string.ui_copy_failed_09fef5d8
    "Fallback search engine" -> R.string.ui_copy_fallback_search_engine_d73e34cd
    "Fastest, light reasoning" -> R.string.ui_copy_fastest_light_reasoning_35172b49
    "Favorites" -> R.string.ui_copy_favorites_07b3e447
    "Fetch models again" -> R.string.ui_copy_fetch_models_again_74312e71
    "Fetched source" -> R.string.ui_copy_fetched_source_abeb9df8
    "Fetching OpenRouter capabilities, reasoning modes, limits, and pricing…" -> R.string.ui_copy_fetching_openrouter_capabilities_reasoning_cf8061d2
    "File no longer exists" -> R.string.ui_copy_file_no_longer_exists_e4b7144f
    "Files" -> R.string.ui_copy_files_6ce6c512
    "Finish" -> R.string.ui_copy_finish_b74bdee9
    "Finish setup" -> R.string.ui_copy_finish_setup_c516d2df
    "Finished with an error" -> R.string.ui_copy_finished_with_an_error_f44e1f9f
    "Fit image" -> R.string.ui_copy_fit_image_5fbe15d2
    "Flat" -> R.string.ui_copy_flat_79c9e13a
    "Focused defaults" -> R.string.ui_copy_focused_defaults_55bb4714
    "Folder selection was cancelled." -> R.string.ui_copy_folder_selection_was_cancelled_ede5fd73
    "Follow device" -> R.string.ui_copy_follow_device_368f3844
    "Frame pacing / scheduling stalls" -> R.string.ui_copy_frame_pacing_scheduling_stalls_b1f9d374
    "Free" -> R.string.ui_copy_free_75f52718
    "From chat" -> R.string.ui_copy_from_chat_6f39019a
    "GPU rendering" -> R.string.ui_copy_gpu_rendering_5e0fb926
    "GPU rendering (blur active)" -> R.string.ui_copy_gpu_rendering_blur_active_6e0bee33
    "Generate + edit" -> R.string.ui_copy_generate_edit_09558a5e
    "Generate image" -> R.string.ui_copy_generate_image_ecfa4a83
    "Generate images" -> R.string.ui_copy_generate_images_c96a6e96
    "Generated UI safety and local-data behavior" -> R.string.ui_copy_generated_ui_safety_and_local_data_behavio_07e16dad
    "Generated content" -> R.string.ui_copy_generated_content_265611d8
    "Generated widgets are paused and shown as safe fallback content." -> R.string.ui_copy_generated_widgets_are_paused_and_shown_as__7189218f
    "Generated widgets may render, but Turp still applies its capability checks and crash recovery." -> R.string.ui_copy_generated_widgets_may_render_but_turp_st_9a434e3e
    "Generating image" -> R.string.ui_copy_generating_image_6adaae43
    "Generating image…" -> R.string.ui_copy_generating_image_2ff63fd3
    "GitHub release workflows embed their own owner/repository. Fork builds therefore follow the fork they came from." -> R.string.ui_copy_github_release_workflows_embed_their_own_o_777dfc18
    "Global answer preferences. Changes apply to existing chats and new chats on their next response." -> R.string.ui_copy_global_answer_preferences_changes_apply_to_8f1f460c
    "Go to latest message" -> R.string.ui_copy_go_to_latest_message_5d5b7490
    "Google Drive" -> R.string.ui_copy_google_drive_07c2964c
    "Google Drive app storage" -> R.string.ui_copy_google_drive_app_storage_e270d4d4
    "Google Drive authorization could not be opened" -> R.string.ui_copy_google_drive_authorization_could_not_be_op_5d66920e
    "Google Drive authorization could not be opened." -> R.string.ui_copy_google_drive_authorization_could_not_be_op_ff074ee2
    "Google Drive authorization expired" -> R.string.ui_copy_google_drive_authorization_expired_9d586208
    "Google Drive authorization failed" -> R.string.ui_copy_google_drive_authorization_failed_4bcf374d
    "Google Drive authorization returned no access token" -> R.string.ui_copy_google_drive_authorization_returned_no_acc_dafecfb6
    "Google Drive backup failed" -> R.string.ui_copy_google_drive_backup_failed_fea439ba
    "Google Drive connection was canceled." -> R.string.ui_copy_google_drive_connection_was_canceled_50affc01
    "Google Drive could not be disconnected" -> R.string.ui_copy_google_drive_could_not_be_disconnected_924c637c
    "Google Drive error" -> R.string.ui_copy_google_drive_error_7b00a536
    "Google Drive registration details copied" -> R.string.ui_copy_google_drive_registration_details_copied_640918d3
    "Google Drive sign-in was cancelled." -> R.string.ui_copy_google_drive_sign_in_was_cancelled_fe06a3e7
    "Google Drive, OneDrive, Nextcloud, USB, or local storage through Android" -> R.string.ui_copy_google_drive_onedrive_nextcloud_usb_or_loc_be9ac574
    "Google account selected" -> R.string.ui_copy_google_account_selected_4da38c66
    "Google returned no access token. Check that this app's package name and signing certificate are registered for its OAuth client." -> R.string.ui_copy_google_returned_no_access_token_check_that_7a54ada7
    "Graphite" -> R.string.ui_copy_graphite_220c98de
    "Guide thickness" -> R.string.ui_copy_guide_thickness_900ac19c
    "Guides are bright red and diagnostic-only. They are never shown unless both Developer settings and this toggle are enabled." -> R.string.ui_copy_guides_are_bright_red_and_diagnostic_only__9d5d4df1
    "HTTPS endpoint" -> R.string.ui_copy_https_endpoint_90787557
    "Hard" -> R.string.ui_copy_hard_20a89915
    "Hidden by tint" -> R.string.ui_copy_hidden_by_tint_2277c4cd
    "Hide OCR overlay" -> R.string.ui_copy_hide_ocr_overlay_8b977e00
    "Hide attempts" -> R.string.ui_copy_hide_attempts_fc111181
    "Hide manual model entry" -> R.string.ui_copy_hide_manual_model_entry_4e453621
    "High" -> R.string.ui_copy_high_b1a5954a
    "Hybrid preflight counting" -> R.string.ui_copy_hybrid_preflight_counting_a5a65d41
    "Hybrid token counting" -> R.string.ui_copy_hybrid_token_counting_cfb44fd8
    "Idle / no rendered frames" -> R.string.ui_copy_idle_no_rendered_frames_5f84c7e4
    "Image billing" -> R.string.ui_copy_image_billing_e900e283
    "Image editing" -> R.string.ui_copy_image_editing_2904f6c6
    "Image generation" -> R.string.ui_copy_image_generation_eef91184
    "Image generation and editing models are kept separate from chat models" -> R.string.ui_copy_image_generation_and_editing_models_are_ke_dd25f3c1
    "Image generation · describe the image you want to create" -> R.string.ui_copy_image_generation_describe_the_image_you_wa_2b0a9f11
    "Image model" -> R.string.ui_copy_image_model_ec9bc150
    "Image request" -> R.string.ui_copy_image_request_ce977f3c
    "Import" -> R.string.ui_copy_import_d6fbc9d2
    "Import Turp backup" -> R.string.ui_copy_import_turp_backup_32a86156
    "Import and continue" -> R.string.ui_copy_import_and_continue_2d344c5b
    "Import backup" -> R.string.ui_copy_import_backup_1b40a3ad
    "Import creates separate local copies. It never replaces an existing chat and does not import API keys or OAuth sessions." -> R.string.ui_copy_import_creates_separate_local_copies_it_ne_68b9d377
    "Import failed" -> R.string.ui_copy_import_failed_fbae8929
    "Import verification passed." -> R.string.ui_copy_import_verification_passed_8fcc4196
    "Import verification warning:" -> R.string.ui_copy_import_verification_warning_184fb69c
    "Imports are non-destructive: every chat is created as a separate copy. Existing chats are never overwritten. Provider credentials are not imported, so reconnect the required provider before continuing an imported chat." -> R.string.ui_copy_imports_are_non_destructive_every_chat_is__a020f13d
    "In use" -> R.string.ui_copy_in_use_20716e92
    "Include app settings and configuration" -> R.string.ui_copy_include_app_settings_and_configuration_c8732e79
    "Include attachments" -> R.string.ui_copy_include_attachments_75c1ce31
    "Include custom system prompts" -> R.string.ui_copy_include_custom_system_prompts_93674a85
    "Include installed Linux environments" -> R.string.ui_copy_include_installed_linux_environments_ba43ca74
    "Include reasoning, tool traces, and request metadata" -> R.string.ui_copy_include_reasoning_tool_traces_and_request__27d3f602
    "Included content" -> R.string.ui_copy_included_content_19f75423
    "Included modules" -> R.string.ui_copy_included_modules_5a882ced
    "Includes theme, UI behavior, new-chat defaults, provider endpoints/models, projects, prompt profiles, and automation settings. Credentials, OAuth sessions, provider authorization headers, cloud grants, drafts, and navigation state stay excluded." -> R.string.ui_copy_includes_theme_ui_behavior_new_chat_defaul_8fd6ca72
    "Input" -> R.string.ui_copy_input_b568d47f
    "Input handling" -> R.string.ui_copy_input_handling_8cf15d8f
    "Input tokens" -> R.string.ui_copy_input_tokens_92f7d222
    "Inspect environments, install packages, run a test, or add/remove the optional Linux distribution." -> R.string.ui_copy_inspect_environments_install_packages_run__d21d8fff
    "Install" -> R.string.ui_copy_install_fd6c3ebf
    "Install Linux" -> R.string.ui_copy_install_linux_f477b4b7
    "Install Ubuntu, Debian, or Alpine before Turp can use Linux tools." -> R.string.ui_copy_install_ubuntu_debian_or_alpine_before_xyl_cfa5e8a8
    "Install a Linux workspace before enabling" -> R.string.ui_copy_install_a_linux_workspace_before_enabling_2029a1e6
    "Install blocked" -> R.string.ui_copy_install_blocked_65cb6dde
    "Install every valid preflight plan without asking" -> R.string.ui_copy_install_every_valid_preflight_plan_without_fb125aee
    "Install failed" -> R.string.ui_copy_install_failed_50c51c74
    "Install package" -> R.string.ui_copy_install_package_cb1fe356
    "Install packages" -> R.string.ui_copy_install_packages_72c740dd
    "Installation and import verification completed." -> R.string.ui_copy_installation_and_import_verification_compl_3ced88e7
    "Installation completed successfully." -> R.string.ui_copy_installation_completed_successfully_390836a3
    "Installation failed" -> R.string.ui_copy_installation_failed_346500a9
    "Installation failed. No success was recorded." -> R.string.ui_copy_installation_failed_no_success_was_recorde_3e818a77
    "Installation or import verification failed." -> R.string.ui_copy_installation_or_import_verification_failed_8448d350
    "Installation started" -> R.string.ui_copy_installation_started_3217fdbf
    "Installed Linux environments" -> R.string.ui_copy_installed_linux_environments_e16d8317
    "Installed packages" -> R.string.ui_copy_installed_packages_1c9814b3
    "Installed, but import verification found a problem" -> R.string.ui_copy_installed_but_import_verification_found_a__7ea653e6
    "Installing approved changes…" -> R.string.ui_copy_installing_approved_changes_faeccffa
    "Installing…" -> R.string.ui_copy_installing_8d278823
    "Instructions" -> R.string.ui_copy_instructions_ed58f297
    "Intelligence" -> R.string.ui_copy_intelligence_c698f940
    "Interface panels" -> R.string.ui_copy_interface_panels_84c17fa8
    "Interrupted response" -> R.string.ui_copy_interrupted_response_4fca3cbd
    "Invalid completed widgets, charts, and diagrams are repaired in place up to this limit." -> R.string.ui_copy_invalid_completed_widgets_charts_and_diagr_81c17c7f
    "Its complete message history, attachments, and local code workspace records will be removed from Turp." -> R.string.ui_copy_its_complete_message_history_attachments_a_2bc529c4
    "Its encrypted OAuth session and models will be disconnected. Chats and usage history are kept." -> R.string.ui_copy_its_encrypted_oauth_session_and_models_wil_ce3a7c79
    "Its saved API key will be erased and it will disappear from model selectors. Chats and usage history are kept." -> R.string.ui_copy_its_saved_api_key_will_be_erased_and_it_wi_6ee2c51a
    "Keep Turp open" -> R.string.ui_copy_keep_turp_open_ba99f421
    "Keep in background" -> R.string.ui_copy_keep_in_background_eef84cf4
    "Keep the classic Turp green icon regardless of the selected palette." -> R.string.ui_copy_keep_the_classic_turp_green_icon_regardl_a53d36aa
    "Keep the partial answer" -> R.string.ui_copy_keep_the_partial_answer_4335f8b2
    "Keyless endpoint" -> R.string.ui_copy_keyless_endpoint_c68df92f
    "Known calculated cost" -> R.string.ui_copy_known_calculated_cost_5822cfd8
    "Last message pairs" -> R.string.ui_copy_last_message_pairs_e97c6325
    "Last request/answer pairs" -> R.string.ui_copy_last_request_answer_pairs_69da861f
    "Layout / measure" -> R.string.ui_copy_layout_measure_5aa5d886
    "Legal" -> R.string.ui_copy_legal_902c91d9
    "Less" -> R.string.ui_copy_less_526cb742
    "Less emoji" -> R.string.ui_copy_less_emoji_7a1a0743
    "License catalog unavailable" -> R.string.ui_copy_license_catalog_unavailable_b7e30cb7
    "License text" -> R.string.ui_copy_license_text_a991ca44
    "Licenses" -> R.string.ui_copy_licenses_ee92c8a4
    "Licenses & notices" -> R.string.ui_copy_licenses_notices_017fc334
    "Light" -> R.string.ui_copy_light_a36ef8ab
    "Linux command" -> R.string.ui_copy_linux_command_fc0366c1
    "Linux commands" -> R.string.ui_copy_linux_commands_3d1af73f
    "Linux distribution" -> R.string.ui_copy_linux_distribution_a7b16608
    "Linux environment" -> R.string.ui_copy_linux_environment_f430f5c4
    "Linux execution" -> R.string.ui_copy_linux_execution_3c4f9e92
    "Linux package request" -> R.string.ui_copy_linux_package_request_643ee6b9
    "Linux terminal" -> R.string.ui_copy_linux_terminal_44a5aa10
    "Linux workspace" -> R.string.ui_copy_linux_workspace_235562e0
    "Linux workspace not installed" -> R.string.ui_copy_linux_workspace_not_installed_eed9c748
    "Linux workspace not ready" -> R.string.ui_copy_linux_workspace_not_ready_cac52e8c
    "Linux · Not installed" -> R.string.ui_copy_linux_not_installed_e6b9395a
    "Live output updates as the process prints." -> R.string.ui_copy_live_output_updates_as_the_process_prints_2130a011
    "Live previews" -> R.string.ui_copy_live_previews_6a5ddb0b
    "Loading preview…" -> R.string.ui_copy_loading_preview_1b6d8c99
    "Loading usage…" -> R.string.ui_copy_loading_usage_6346c797
    "Loading…" -> R.string.ui_copy_loading_33ce4174
    "Local Code Execution" -> R.string.ui_copy_local_code_execution_03e5ad9d
    "Local backup" -> R.string.ui_copy_local_backup_14278997
    "Local code execution" -> R.string.ui_copy_local_code_execution_dba7a428
    "Local description" -> R.string.ui_copy_local_description_a14ecdef
    "Local diagnostics for measuring Turp's rendering and process performance. No metrics are uploaded or stored in chat history." -> R.string.ui_copy_local_diagnostics_for_measuring_turp_s_r_7859cb0b
    "Local execution" -> R.string.ui_copy_local_execution_f3ca65f0
    "Local execution is opt-in for fresh installs. Existing chats keep their own tool choices." -> R.string.ui_copy_local_execution_is_opt_in_for_fresh_instal_bd33adc8
    "Local execution starts off; enable Python or Linux only when a chat needs it." -> R.string.ui_copy_local_execution_starts_off_enable_python_o_fc0fc181
    "Local image description" -> R.string.ui_copy_local_image_description_b56e4b46
    "Local server" -> R.string.ui_copy_local_server_444ff973
    "Local • no API call" -> R.string.ui_copy_local_no_api_call_b50a29f9
    "Low" -> R.string.ui_copy_low_a124947c
    "Manage Linux workspace" -> R.string.ui_copy_manage_linux_workspace_a1b97808
    "Manage providers" -> R.string.ui_copy_manage_providers_dcb194f5
    "Manual" -> R.string.ui_copy_manual_4e836fdc
    "Manual memories are available immediately and use the same deduplication rules." -> R.string.ui_copy_manual_memories_are_available_immediately__90724a6f
    "Manual model will also be included" -> R.string.ui_copy_manual_model_will_also_be_included_d635d003
    "Match launcher icon to palette" -> R.string.ui_copy_match_launcher_icon_to_palette_d26f874d
    "Max" -> R.string.ui_copy_max_a95e85ae
    "Max output" -> R.string.ui_copy_max_output_86685691
    "Maximum output tokens" -> R.string.ui_copy_maximum_output_tokens_dc95e9b0
    "Maximum search results" -> R.string.ui_copy_maximum_search_results_e61513a8
    "Maximum supported reasoning" -> R.string.ui_copy_maximum_supported_reasoning_72f61320
    "Medium" -> R.string.ui_copy_medium_d404968e
    "Memory" -> R.string.ui_copy_memory_89c8a285
    "Message Turp…" -> R.string.ui_copy_message_turp_5bdb2e7f
    "Message actions" -> R.string.ui_copy_message_actions_949184b0
    "Messages, code, and reasoning are searched locally as you type." -> R.string.ui_copy_messages_code_and_reasoning_are_searched_l_e9813ab2
    "Minimal" -> R.string.ui_copy_minimal_a711cca9
    "Minimum Android" -> R.string.ui_copy_minimum_android_b09a62b8
    "Mixed / unattributed frame work" -> R.string.ui_copy_mixed_unattributed_frame_work_75867d1d
    "Model" -> R.string.ui_copy_model_68c2cc7f
    "Model access" -> R.string.ui_copy_model_access_a261fa70
    "Model access connected" -> R.string.ui_copy_model_access_connected_7278bf38
    "Model capabilities and request transport are selected automatically by this provider preset." -> R.string.ui_copy_model_capabilities_and_request_transport_a_e8afbbc0
    "Model display name" -> R.string.ui_copy_model_display_name_4a1c8150
    "Model mode considers newer messages whenever a name is regenerated." -> R.string.ui_copy_model_mode_considers_newer_messages_whenev_77e6cf3d
    "Model provider" -> R.string.ui_copy_model_provider_a98fe960
    "Model receives OCR fallback • original preview is unchanged" -> R.string.ui_copy_model_receives_ocr_fallback_original_previ_720c2fa1
    "Model refresh failed" -> R.string.ui_copy_model_refresh_failed_66a368ae
    "Model review is advisory and can be wrong. Turp records the selected model's allow/deny reason, but this is not malware analysis or a security guarantee." -> R.string.ui_copy_model_review_is_advisory_and_can_be_wrong__5aa84b0e
    "Model, thinking, context, and output limits" -> R.string.ui_copy_model_thinking_context_and_output_limits_a300db3e
    "Models" -> R.string.ui_copy_models_f3798f81
    "Models discovered for this ChatGPT account only." -> R.string.ui_copy_models_discovered_for_this_chatgpt_account_d0054503
    "Models from provider" -> R.string.ui_copy_models_from_provider_8d5cb90d
    "More" -> R.string.ui_copy_more_4bab2d8f
    "More thorough reasoning" -> R.string.ui_copy_more_thorough_reasoning_13dc0c45
    "Move chat" -> R.string.ui_copy_move_chat_77b920ba
    "Move to project" -> R.string.ui_copy_move_to_project_ce1b439b
    "My DeepSeek account" -> R.string.ui_copy_my_deepseek_account_9470faf9
    "Name" -> R.string.ui_copy_name_709a2322
    "Native Android BYOK model workspace." -> R.string.ui_copy_native_android_byok_model_workspace_1424a951
    "Native compatibility warning:" -> R.string.ui_copy_native_compatibility_warning_6d5b6b5d
    "Native routing, search engines, credentials, and page fetching" -> R.string.ui_copy_native_routing_search_engines_credentials__02119a58
    "New chat" -> R.string.ui_copy_new_chat_009bf6b9
    "New chat defaults" -> R.string.ui_copy_new_chat_defaults_701b0334
    "New custom profile" -> R.string.ui_copy_new_custom_profile_c3ac50e4
    "New project" -> R.string.ui_copy_new_project_e5bcbe24
    "Next" -> R.string.ui_copy_next_bc981983
    "Next branch" -> R.string.ui_copy_next_branch_df6b5482
    "Next extracted-text page" -> R.string.ui_copy_next_extracted_text_page_ee3812a2
    "Next page" -> R.string.ui_copy_next_page_4bfc194b
    "Next text page" -> R.string.ui_copy_next_text_page_83390592
    "Nextcloud" -> R.string.ui_copy_nextcloud_aab73a3d
    "Nextcloud / WebDAV" -> R.string.ui_copy_nextcloud_webdav_0869236b
    "No GitHub source was embedded in this build" -> R.string.ui_copy_no_github_source_was_embedded_in_this_buil_ca48df2c
    "No Turp account, ads, analytics, or Turp cloud. Chat history and API keys remain on this device; traffic goes to endpoints and web tools you explicitly enable." -> R.string.ui_copy_no_turp_account_ads_analytics_or_turp__58797359
    "No additional diagnostic text was returned by the provider." -> R.string.ui_copy_no_additional_diagnostic_text_was_returned_04b2ab87
    "No backup file selected." -> R.string.ui_copy_no_backup_file_selected_11ac152a
    "No chat workspace is available" -> R.string.ui_copy_no_chat_workspace_is_available_4a5eaecf
    "No chats in this project yet." -> R.string.ui_copy_no_chats_in_this_project_yet_e5f6eaab
    "No context was compressed" -> R.string.ui_copy_no_context_was_compressed_ca27a63f
    "No conversation" -> R.string.ui_copy_no_conversation_b809a663
    "No image models available" -> R.string.ui_copy_no_image_models_available_8b362ea7
    "No inline preview for this file type" -> R.string.ui_copy_no_inline_preview_for_this_file_type_b944275b
    "No matches" -> R.string.ui_copy_no_matches_cd0af6cf
    "No matching chat models" -> R.string.ui_copy_no_matching_chat_models_9f9e1f23
    "No matching chats or messages." -> R.string.ui_copy_no_matching_chats_or_messages_5f0fbeb7
    "No matching components" -> R.string.ui_copy_no_matching_components_2db7cc05
    "No matching image models" -> R.string.ui_copy_no_matching_image_models_912a73d8
    "No matching models." -> R.string.ui_copy_no_matching_models_7913af03
    "No memories match the current filters." -> R.string.ui_copy_no_memories_match_the_current_filters_f5ace8ee
    "No memories saved yet." -> R.string.ui_copy_no_memories_saved_yet_91f7ce7c
    "No output." -> R.string.ui_copy_no_output_64bbb2d2
    "No package transaction running." -> R.string.ui_copy_no_package_transaction_running_b70454ca
    "No password: backup remains readable to anyone who gets the file. This is allowed; choose a trusted destination." -> R.string.ui_copy_no_password_backup_remains_readable_to_any_b96d0c4e
    "No password: the recipient can open the file immediately. This is allowed, but anyone with the file can read the included content." -> R.string.ui_copy_no_password_the_recipient_can_open_the_fil_af2eebe0
    "No per-call usage rows were recorded for this response. The aggregate values above are still available." -> R.string.ui_copy_no_per_call_usage_rows_were_recorded_for_t_c03cc627
    "No project" -> R.string.ui_copy_no_project_c326b757
    "No provider selected" -> R.string.ui_copy_no_provider_selected_461348e5
    "No providers yet" -> R.string.ui_copy_no_providers_yet_819a57da
    "No result details" -> R.string.ui_copy_no_result_details_e49ecfd2
    "No results" -> R.string.ui_copy_no_results_b993b0c5
    "No saved prompts yet." -> R.string.ui_copy_no_saved_prompts_yet_b63aec70
    "No search results were returned." -> R.string.ui_copy_no_search_results_were_returned_b2c034b3
    "Non-cached input" -> R.string.ui_copy_non_cached_input_bd695a67
    "None" -> R.string.ui_copy_none_6eef6648
    "Not connected yet" -> R.string.ui_copy_not_connected_yet_d98c2e33
    "Not embedded" -> R.string.ui_copy_not_embedded_de9782a4
    "Not supported by this model" -> R.string.ui_copy_not_supported_by_this_model_4fe32d8b
    "Nothing is stored yet." -> R.string.ui_copy_nothing_is_stored_yet_ffb95486
    "Notices" -> R.string.ui_copy_notices_e94865d4
    "OCR fallback enabled" -> R.string.ui_copy_ocr_fallback_enabled_70d43930
    "OCR on" -> R.string.ui_copy_ocr_on_1c6e6a86
    "OCR on send" -> R.string.ui_copy_ocr_on_send_57d8f20e
    "OCR ready" -> R.string.ui_copy_ocr_ready_aaaa70a0
    "Ocean" -> R.string.ui_copy_ocean_76022a00
    "Off" -> R.string.ui_copy_off_e3de5ab0
    "Off by default. Normal chats show only a concise failure summary and Retry." -> R.string.ui_copy_off_by_default_normal_chats_show_only_a_co_d4fbcc0f
    "Offline dependency catalog and full license texts" -> R.string.ui_copy_offline_dependency_catalog_and_full_licens_7fff5d96
    "Older messages outside the active context window are merged into saved compact context." -> R.string.ui_copy_older_messages_outside_the_active_context__8f176d19
    "On" -> R.string.ui_copy_on_e0049a66
    "One package requirement per line. Turp resolves Android-compatible Python 3.12 wheels before applying your approval policy." -> R.string.ui_copy_one_package_requirement_per_line_turp_re_2cdc609b
    "One searchable catalog is used everywhere in Turp." -> R.string.ui_copy_one_searchable_catalog_is_used_everywhere__3b52403e
    "One-tap native OAuth. Turp opens the system browser, receives the localhost callback itself, encrypts the session on this device, and refreshes it automatically. No extension or local proxy is required." -> R.string.ui_copy_one_tap_native_oauth_turp_opens_the_syst_028265ea
    "OneDrive" -> R.string.ui_copy_onedrive_80753c98
    "OneDrive Apps/Turp" -> R.string.ui_copy_onedrive_apps_turp_65f8fd13
    "Only missing or outdated packages will be changed. Packages and install scripts run with Turp's app permissions." -> R.string.ui_copy_only_missing_or_outdated_packages_will_be__36387bb4
    "Only missing or outdated packages will change. Install scripts run with Turp's app permissions." -> R.string.ui_copy_only_missing_or_outdated_packages_will_cha_0024de76
    "Only the essentials are shown. Pricing is optional." -> R.string.ui_copy_only_the_essentials_are_shown_pricing_is_o_ec6a64b1
    "Only the selected provider models will be saved." -> R.string.ui_copy_only_the_selected_provider_models_will_be__d8bf05dd
    "Only this page is held in memory. Save or share the file for full-file processing." -> R.string.ui_copy_only_this_page_is_held_in_memory_save_or_s_f543dcb1
    "Open" -> R.string.ui_copy_open_cf9b7706
    "Open OCR view" -> R.string.ui_copy_open_ocr_view_94133154
    "Open Turp archive" -> R.string.ui_copy_open_turp_archive_925f05d2
    "Open conversations" -> R.string.ui_copy_open_conversations_65aafc8c
    "Open full-screen preview" -> R.string.ui_copy_open_full_screen_preview_54e1aea4
    "Open navigation drawer" -> R.string.ui_copy_open_navigation_drawer_73502976
    "Open release page" -> R.string.ui_copy_open_release_page_f45461ef
    "Open runtime manager" -> R.string.ui_copy_open_runtime_manager_8cabaae0
    "Open settings" -> R.string.ui_copy_open_settings_fd7108f8
    "Open terminal" -> R.string.ui_copy_open_terminal_b4ae7862
    "Open the creator's GitHub profile" -> R.string.ui_copy_open_the_creator_s_github_profile_1882d59e
    "Open-source licenses" -> R.string.ui_copy_open_source_licenses_30dced67
    "Opened by Turp" -> R.string.ui_copy_opened_by_turp_809ef254
    "Opening Android's folder picker…" -> R.string.ui_copy_opening_android_s_folder_picker_e7e6b80d
    "Opening backup preview…" -> R.string.ui_copy_opening_backup_preview_9e9080e2
    "Opening file picker…" -> R.string.ui_copy_opening_file_picker_a46eeb43
    "Optional" -> R.string.ui_copy_optional_0c6c4102
    "Optional download for tools that cannot run in bundled Python" -> R.string.ui_copy_optional_download_for_tools_that_cannot_ru_424205bd
    "Optional hybrid preflight counting. Provider count endpoints are preferred; local model-family estimates and the generic estimator are fallbacks." -> R.string.ui_copy_optional_hybrid_preflight_counting_provide_5014ff78
    "Optional tools stay optional" -> R.string.ui_copy_optional_tools_stay_optional_211fceae
    "Optional · cost will show as unavailable" -> R.string.ui_copy_optional_cost_will_show_as_unavailable_e5007e46
    "Optional. Preview a local or cloud backup before importing anything." -> R.string.ui_copy_optional_preview_a_local_or_cloud_backup_b_4e44eb02
    "Original image • model receives OCR fallback" -> R.string.ui_copy_original_image_model_receives_ocr_fallback_dcc91201
    "Original image • selected model cannot receive it" -> R.string.ui_copy_original_image_selected_model_cannot_recei_70046764
    "Output" -> R.string.ui_copy_output_4bed3361
    "Output / billed tokens" -> R.string.ui_copy_output_billed_tokens_c9b801d8
    "Output limit" -> R.string.ui_copy_output_limit_63e1b0a1
    "Output tokens" -> R.string.ui_copy_output_tokens_b879f52d
    "Overlay position" -> R.string.ui_copy_overlay_position_a0a2016e
    "Overlay scale" -> R.string.ui_copy_overlay_scale_63c16ba8
    "Override" -> R.string.ui_copy_override_84219262
    "Override default tone/persona" -> R.string.ui_copy_override_default_tone_persona_1c38309c
    "PROJECT CHATS" -> R.string.ui_copy_project_chats_a3ee1035
    "PROJECTS" -> R.string.ui_copy_projects_1742f5d5
    "PYTHON CODE" -> R.string.ui_copy_python_code_6cb24884
    "Package" -> R.string.ui_copy_package_7431e3df
    "Package approval" -> R.string.ui_copy_package_approval_a1572fcf
    "Package changes" -> R.string.ui_copy_package_changes_6ba20f47
    "Package installation" -> R.string.ui_copy_package_installation_77ca8471
    "Package installation failed" -> R.string.ui_copy_package_installation_failed_fa582e56
    "Package manager" -> R.string.ui_copy_package_manager_a069a02e
    "Package review" -> R.string.ui_copy_package_review_d72d29e6
    "Packages" -> R.string.ui_copy_packages_0a999012
    "Packages and their installers run with Turp's app permissions. Ubuntu is for compatibility, not containment; these settings intentionally reduce confirmation barriers." -> R.string.ui_copy_packages_and_their_installers_run_with_xyl_fbb64874
    "Panel opacity" -> R.string.ui_copy_panel_opacity_e2eed655
    "Panel shape" -> R.string.ui_copy_panel_shape_504daac7
    "Panel shape is a choice. Blur, softness, and tint remain continuous controls." -> R.string.ui_copy_panel_shape_is_a_choice_blur_softness_and__7e45858a
    "Password" -> R.string.ui_copy_password_8be3c943
    "Password (optional)" -> R.string.ui_copy_password_optional_408255ed
    "Password or app password" -> R.string.ui_copy_password_or_app_password_e9577bc0
    "Password — leave blank for none" -> R.string.ui_copy_password_leave_blank_for_none_3fcb8176
    "Passwords do not match" -> R.string.ui_copy_passwords_do_not_match_d69c3b1a
    "Pause" -> R.string.ui_copy_pause_781961bc
    "Paused" -> R.string.ui_copy_paused_c7dfb6f1
    "Pending image requests" -> R.string.ui_copy_pending_image_requests_91ac1b3c
    "Per-response call breakdowns are available from each assistant message's ⋮ menu." -> R.string.ui_copy_per_response_call_breakdowns_are_available_da1e5296
    "Performance counter" -> R.string.ui_copy_performance_counter_867144eb
    "Personalization" -> R.string.ui_copy_personalization_f203458e
    "Pin" -> R.string.ui_copy_pin_9c918414
    "Plan ready for your confirmation." -> R.string.ui_copy_plan_ready_for_your_confirmation_7f5831cb
    "Portable Turp backup" -> R.string.ui_copy_portable_turp_backup_27aec9c6
    "Portable cloud backups can include chats, app settings, organization, and optional Linux root filesystems. Passwordless backups are allowed after an explicit warning; API keys, OAuth sessions, provider authorization headers, cloud grants, and database encryption keys are excluded." -> R.string.ui_copy_portable_cloud_backups_can_include_chats_a_4e7a5c3f
    "Preflight & review" -> R.string.ui_copy_preflight_review_25740cd2
    "Preflight failed" -> R.string.ui_copy_preflight_failed_10168b8c
    "Preparing Turp…" -> R.string.ui_copy_preparing_turp_25621d30
    "Preparing file" -> R.string.ui_copy_preparing_file_17b2d5d2
    "Preparing package plan…" -> R.string.ui_copy_preparing_package_plan_a7d922a2
    "Preparing query" -> R.string.ui_copy_preparing_query_03c56532
    "Preparing…" -> R.string.ui_copy_preparing_0b1d6c0b
    "Prepend" -> R.string.ui_copy_prepend_9d1c9745
    "Preset: Custom" -> R.string.ui_copy_preset_custom_2d5c0a86
    "Preview" -> R.string.ui_copy_preview_f1fbb2b4
    "Preview and import a backup" -> R.string.ui_copy_preview_and_import_a_backup_bbae34ab
    "Previous" -> R.string.ui_copy_previous_50f94286
    "Previous branch" -> R.string.ui_copy_previous_branch_4855c3fe
    "Previous extracted-text page" -> R.string.ui_copy_previous_extracted_text_page_52a768b0
    "Previous page" -> R.string.ui_copy_previous_page_81f54719
    "Previous setup step" -> R.string.ui_copy_previous_setup_step_d27486e7
    "Previous text page" -> R.string.ui_copy_previous_text_page_ec053f3a
    "Previously selected backup folder" -> R.string.ui_copy_previously_selected_backup_folder_44c73e1b
    "Pricing" -> R.string.ui_copy_pricing_a0d9bbad
    "Pricing configured" -> R.string.ui_copy_pricing_configured_71531171
    "Privacy" -> R.string.ui_copy_privacy_cf01481f
    "Privacy & safety" -> R.string.ui_copy_privacy_safety_3c5b980b
    "Privacy policy" -> R.string.ui_copy_privacy_policy_7ceacdca
    "Privacy, local data, providers, and KVKK/GDPR boundaries" -> R.string.ui_copy_privacy_local_data_providers_and_kvkk_gdpr_fb4f9a28
    "Private Turp app storage" -> R.string.ui_copy_private_turp_app_storage_d537b698
    "Private by design" -> R.string.ui_copy_private_by_design_3aaa9fd1
    "Private cloud targets" -> R.string.ui_copy_private_cloud_targets_d3ad80b3
    "Profiler disabled" -> R.string.ui_copy_profiler_disabled_26604127
    "Project" -> R.string.ui_copy_project_f6f4da8d
    "Project website" -> R.string.ui_copy_project_website_21cd5ee2
    "Provider calls" -> R.string.ui_copy_provider_calls_d9cbf2c7
    "Provider credentials" -> R.string.ui_copy_provider_credentials_6f604704
    "Provider details unavailable" -> R.string.ui_copy_provider_details_unavailable_b5b239d7
    "Provider has no model list? Enter manually" -> R.string.ui_copy_provider_has_no_model_list_enter_manually_7a67fcd7
    "Provider is missing" -> R.string.ui_copy_provider_is_missing_ccc7875a
    "Provider name" -> R.string.ui_copy_provider_name_34ea99e9
    "Provider name is required" -> R.string.ui_copy_provider_name_is_required_1603ad44
    "Provider native search" -> R.string.ui_copy_provider_native_search_3568016c
    "Provider preview" -> R.string.ui_copy_provider_preview_ce3ddaf8
    "Provider search" -> R.string.ui_copy_provider_search_162a2dea
    "Provider settings" -> R.string.ui_copy_provider_settings_afda9c2e
    "Providers" -> R.string.ui_copy_providers_87b7c08b
    "Providers & models" -> R.string.ui_copy_providers_models_f3a00666
    "Public HTTPS base URL" -> R.string.ui_copy_public_https_base_url_2ade831c
    "Python" -> R.string.ui_copy_python_6e360488
    "Python and Linux are managed later from Settings → Local execution." -> R.string.ui_copy_python_and_linux_are_managed_later_from_se_f5a239d2
    "Python and Linux share this chat's private /workspace. Runtime setup, packages, health, tests, and removal have one owner here." -> R.string.ui_copy_python_and_linux_share_this_chat_s_private_c2628367
    "Python environment" -> R.string.ui_copy_python_environment_f513aa68
    "Python execution" -> R.string.ui_copy_python_execution_41df7903
    "Python is built in. Linux is an optional compatibility layer with a separate download. Pick a runtime only when you need to inspect or change it." -> R.string.ui_copy_python_is_built_in_linux_is_an_optional_co_e902c5f4
    "Python is still running" -> R.string.ui_copy_python_is_still_running_be5dc2dc
    "Python package request" -> R.string.ui_copy_python_package_request_638ae108
    "Python result" -> R.string.ui_copy_python_result_52b6f346
    "Python script" -> R.string.ui_copy_python_script_2d49b511
    "Python tool result" -> R.string.ui_copy_python_tool_result_510f1a82
    "Python workspace" -> R.string.ui_copy_python_workspace_8cbf4c4e
    "Python · Ready" -> R.string.ui_copy_python_ready_a22ad463
    "Python, Linux, packages, and approval policy" -> R.string.ui_copy_python_linux_packages_and_approval_policy_2d76c5d1
    "Query ready" -> R.string.ui_copy_query_ready_b3c19922
    "Query unavailable" -> R.string.ui_copy_query_unavailable_8effdfee
    "Queue image" -> R.string.ui_copy_queue_image_83648a73
    "Queue this message" -> R.string.ui_copy_queue_this_message_98929445
    "Queued" -> R.string.ui_copy_queued_6a599877
    "RECENT CHATS" -> R.string.ui_copy_recent_chats_1a3f6f06
    "Raw token totals recorded from provider calls. Use these with the provider's current pricing when you want to verify or calculate cost manually." -> R.string.ui_copy_raw_token_totals_recorded_from_provider_ca_21e8e343
    "Read license" -> R.string.ui_copy_read_license_346f9049
    "Reading source" -> R.string.ui_copy_reading_source_e22a34c2
    "Reading source…" -> R.string.ui_copy_reading_source_124aaf17
    "Ready" -> R.string.ui_copy_ready_20c7c552
    "Ready for your review" -> R.string.ui_copy_ready_for_your_review_034acd78
    "Reasoning" -> R.string.ui_copy_reasoning_e272c597
    "Reasoning details" -> R.string.ui_copy_reasoning_details_2c5f9ae8
    "Recent" -> R.string.ui_copy_recent_76eec760
    "Recently created" -> R.string.ui_copy_recently_created_89bd0512
    "Recently updated" -> R.string.ui_copy_recently_updated_5f0500cc
    "Reconnect" -> R.string.ui_copy_reconnect_6988b16a
    "Reference image" -> R.string.ui_copy_reference_image_d30a01d6
    "Reference images" -> R.string.ui_copy_reference_images_409986b9
    "Referenced file" -> R.string.ui_copy_referenced_file_51954abd
    "Refresh" -> R.string.ui_copy_refresh_56e3badc
    "Refresh backup list" -> R.string.ui_copy_refresh_backup_list_d981b77d
    "Refresh models" -> R.string.ui_copy_refresh_models_ba6da3f2
    "Refresh usage" -> R.string.ui_copy_refresh_usage_a4a94d04
    "Refreshing…" -> R.string.ui_copy_refreshing_96141178
    "Regenerate chat name" -> R.string.ui_copy_regenerate_chat_name_c0f73ee5
    "Region" -> R.string.ui_copy_region_0f217179
    "Reinstall" -> R.string.ui_copy_reinstall_b08a8859
    "Release to snap to the nearest supported level" -> R.string.ui_copy_release_to_snap_to_the_nearest_supported_l_6f40c155
    "Remove" -> R.string.ui_copy_remove_e963907d
    "Remove Linux" -> R.string.ui_copy_remove_linux_336f826b
    "Remove Linux runtime" -> R.string.ui_copy_remove_linux_runtime_5da548c5
    "Remove attachments first · image editing is not enabled yet" -> R.string.ui_copy_remove_attachments_first_image_editing_is__bbda72fc
    "Remove attachments to generate an image" -> R.string.ui_copy_remove_attachments_to_generate_an_image_be70d1ad
    "Remove favorite" -> R.string.ui_copy_remove_favorite_8b9da771
    "Remove image" -> R.string.ui_copy_remove_image_5f94b03c
    "Remove non-image attachments before creating an image." -> R.string.ui_copy_remove_non_image_attachments_before_creati_3345f034
    "Remove package" -> R.string.ui_copy_remove_package_cdf7f6e3
    "Remove provider" -> R.string.ui_copy_remove_provider_d90c83fd
    "Remove provider?" -> R.string.ui_copy_remove_provider_6c067d90
    "Rename" -> R.string.ui_copy_rename_d3f4cb89
    "Rename ChatGPT provider" -> R.string.ui_copy_rename_chatgpt_provider_c475f053
    "Rename chat" -> R.string.ui_copy_rename_chat_3c1a5c62
    "Rename project" -> R.string.ui_copy_rename_project_633018f5
    "Rename provider" -> R.string.ui_copy_rename_provider_d2136648
    "Render command issue" -> R.string.ui_copy_render_command_issue_0a3df05e
    "Render sync" -> R.string.ui_copy_render_sync_5f5c4913
    "Rendering PDF…" -> R.string.ui_copy_rendering_pdf_be45bf92
    "Rendering image" -> R.string.ui_copy_rendering_image_027b8970
    "Repair" -> R.string.ui_copy_repair_5f60b1c2
    "Repair failed" -> R.string.ui_copy_repair_failed_1e8cfc05
    "Repairing…" -> R.string.ui_copy_repairing_0c43c38d
    "Replaced by an edited message" -> R.string.ui_copy_replaced_by_an_edited_message_cdd51fbc
    "Replaced by retry" -> R.string.ui_copy_replaced_by_retry_98216aa4
    "Report an issue" -> R.string.ui_copy_report_an_issue_bd7b4f00
    "Request error" -> R.string.ui_copy_request_error_463914ac
    "Request failed" -> R.string.ui_copy_request_failed_9fcda32c
    "Request metadata" -> R.string.ui_copy_request_metadata_8eb15663
    "Request snapshots and extracted attachment metadata" -> R.string.ui_copy_request_snapshots_and_extracted_attachment_3dcde9e3
    "Request type" -> R.string.ui_copy_request_type_b6933e01
    "Requesting access to Google Drive app storage…" -> R.string.ui_copy_requesting_access_to_google_drive_app_stor_9913552a
    "Require API key" -> R.string.ui_copy_require_api_key_7d4d5afd
    "Required" -> R.string.ui_copy_required_eed6bfb4
    "Requires a separate distribution download" -> R.string.ui_copy_requires_a_separate_distribution_download_b9a68b9d
    "Research in progress" -> R.string.ui_copy_research_in_progress_57847cb3
    "Research request…" -> R.string.ui_copy_research_request_32906815
    "Research roadmap" -> R.string.ui_copy_research_roadmap_33257df1
    "Research state reported" -> R.string.ui_copy_research_state_reported_c8951128
    "Reset" -> R.string.ui_copy_reset_44c57abd
    "Reset advanced values to defaults" -> R.string.ui_copy_reset_advanced_values_to_defaults_cb8c861c
    "Response paused" -> R.string.ui_copy_response_paused_40403cac
    "Response style" -> R.string.ui_copy_response_style_fe235e09
    "Restore" -> R.string.ui_copy_restore_3cbe6d6b
    "Restore a backup" -> R.string.ui_copy_restore_a_backup_8c6bdaa6
    "Restore a backup if you have one, then connect the model provider you actually want to use. Everything optional stays out of your way." -> R.string.ui_copy_restore_a_backup_if_you_have_one_then_conn_466e33b9
    "Restore an existing setup" -> R.string.ui_copy_restore_an_existing_setup_29069d47
    "Restore backup" -> R.string.ui_copy_restore_backup_a65eaa88
    "Restore complete" -> R.string.ui_copy_restore_complete_6e584d57
    "Restore failed" -> R.string.ui_copy_restore_failed_c848a612
    "Restore from cloud" -> R.string.ui_copy_restore_from_cloud_52b66631
    "Restore from file" -> R.string.ui_copy_restore_from_file_0279820c
    "Restore preview" -> R.string.ui_copy_restore_preview_49a9358b
    "Restrained blue-gray palette" -> R.string.ui_copy_restrained_blue_gray_palette_09290612
    "Result exposed by the model provider's search response." -> R.string.ui_copy_result_exposed_by_the_model_provider_s_sea_150c2ff1
    "Results" -> R.string.ui_copy_results_612e12d2
    "Retry" -> R.string.ui_copy_retry_9f5cd8a2
    "Retry response" -> R.string.ui_copy_retry_response_0fb7919f
    "Retry setup" -> R.string.ui_copy_retry_setup_f1ed135c
    "Reusable tone and workflow profiles" -> R.string.ui_copy_reusable_tone_and_workflow_profiles_50d55ba1
    "Review and install changes" -> R.string.ui_copy_review_and_install_changes_9f25ee02
    "Root terminal" -> R.string.ui_copy_root_terminal_5dd56a2a
    "Rounded" -> R.string.ui_copy_rounded_c5aa340f
    "Rounded panels use a hard, rounded boundary. Choose Flat to adjust softness." -> R.string.ui_copy_rounded_panels_use_a_hard_rounded_boundary_ab865b53
    "Routing" -> R.string.ui_copy_routing_7d15dd1b
    "Run" -> R.string.ui_copy_run_b1b39260
    "Run Python" -> R.string.ui_copy_run_python_d24665bd
    "Run Python in this chat's persistent workspace" -> R.string.ui_copy_run_python_in_this_chat_s_persistent_works_8b930a92
    "Run again" -> R.string.ui_copy_run_again_ee1d36fe
    "Run command" -> R.string.ui_copy_run_command_1a58851c
    "Run details" -> R.string.ui_copy_run_details_e6487ca7
    "Run failed" -> R.string.ui_copy_run_failed_9a731f3d
    "Run in workspace" -> R.string.ui_copy_run_in_workspace_8ba20353
    "Run preflight again" -> R.string.ui_copy_run_preflight_again_835684a8
    "Run test" -> R.string.ui_copy_run_test_6ecaed96
    "Run with Linux tools" -> R.string.ui_copy_run_with_linux_tools_31de9bc5
    "Running" -> R.string.ui_copy_running_73989d9c
    "Running on" -> R.string.ui_copy_running_on_68f7347e
    "Running…" -> R.string.ui_copy_running_7ef25a76
    "Runtime" -> R.string.ui_copy_runtime_c4740e4c
    "Runtime manager" -> R.string.ui_copy_runtime_manager_8dcd309d
    "Runtime status" -> R.string.ui_copy_runtime_status_e712f6e9
    "S3-compatible storage" -> R.string.ui_copy_s3_compatible_storage_3a892b1a
    "SHELL COMMAND" -> R.string.ui_copy_shell_command_ba6145d8
    "Safe defaults share visible messages and attachments. Hidden reasoning, tool diagnostics, prompts, and request snapshots stay excluded until you enable them." -> R.string.ui_copy_safe_defaults_share_visible_messages_and_a_d7b0a5f4
    "Safe generated rendering" -> R.string.ui_copy_safe_generated_rendering_884dcfe0
    "Safe mode" -> R.string.ui_copy_safe_mode_c730d398
    "Save" -> R.string.ui_copy_save_efc007a3
    "Save & regenerate" -> R.string.ui_copy_save_regenerate_47306967
    "Save a copy" -> R.string.ui_copy_save_a_copy_9056f96b
    "Save and test" -> R.string.ui_copy_save_and_test_d9c41ad4
    "Save backup" -> R.string.ui_copy_save_backup_b93fe3b0
    "Save connection" -> R.string.ui_copy_save_connection_07796d38
    "Save key" -> R.string.ui_copy_save_key_f5216b3a
    "Save memory" -> R.string.ui_copy_save_memory_d739576e
    "Saved" -> R.string.ui_copy_saved_c0ae8f6e
    "Saved memories" -> R.string.ui_copy_saved_memories_85b44d2e
    "SearXNG endpoint" -> R.string.ui_copy_searxng_endpoint_e8df7cac
    "Search" -> R.string.ui_copy_search_bce06414
    "Search & web" -> R.string.ui_copy_search_web_faa075e7
    "Search PyPI" -> R.string.ui_copy_search_pypi_a41ba176
    "Search across every chat" -> R.string.ui_copy_search_across_every_chat_011ad424
    "Search chat models by name, ID, provider, or description" -> R.string.ui_copy_search_chat_models_by_name_id_provider_or__3f63b483
    "Search chats" -> R.string.ui_copy_search_chats_9ad51dea
    "Search chats and messages" -> R.string.ui_copy_search_chats_and_messages_5fa72b1a
    "Search chats and messages…" -> R.string.ui_copy_search_chats_and_messages_d5ea9c16
    "Search failed" -> R.string.ui_copy_search_failed_e2e904ac
    "Search history" -> R.string.ui_copy_search_history_620fcadd
    "Search image models" -> R.string.ui_copy_search_image_models_1c67b8c0
    "Search libraries or licenses" -> R.string.ui_copy_search_libraries_or_licenses_09d1d9af
    "Search licenses" -> R.string.ui_copy_search_licenses_a38f138b
    "Search memories" -> R.string.ui_copy_search_memories_7948125c
    "Search messages" -> R.string.ui_copy_search_messages_abea65ae
    "Search messages, code, and reasoning" -> R.string.ui_copy_search_messages_code_and_reasoning_72ccbd1c
    "Search models" -> R.string.ui_copy_search_models_5f018fe0
    "Search off" -> R.string.ui_copy_search_off_bd9e4b47
    "Search results" -> R.string.ui_copy_search_results_0144dae8
    "Searching…" -> R.string.ui_copy_searching_1a6a5ba8
    "Secret access key" -> R.string.ui_copy_secret_access_key_3dddfad4
    "Select all" -> R.string.ui_copy_select_all_913afff1
    "Select folder" -> R.string.ui_copy_select_folder_20e97c98
    "Select shown" -> R.string.ui_copy_select_shown_a5dea35d
    "Selectable OCR text" -> R.string.ui_copy_selectable_ocr_text_3b00a9e8
    "Selectable extracted content" -> R.string.ui_copy_selectable_extracted_content_7a290103
    "Selectable file preview" -> R.string.ui_copy_selectable_file_preview_cec997a3
    "Selected" -> R.string.ui_copy_selected_9a976fc2
    "Selected cloud folder" -> R.string.ui_copy_selected_cloud_folder_3b837d26
    "Selected model cannot read the original attachment" -> R.string.ui_copy_selected_model_cannot_read_the_original_at_df95d8a3
    "Send" -> R.string.ui_copy_send_9bc2575c
    "Send after the current response finishes" -> R.string.ui_copy_send_after_the_current_response_finishes_938ad55c
    "Send now" -> R.string.ui_copy_send_now_dae33010
    "Send options" -> R.string.ui_copy_send_options_0ef0ada8
    "Sending…" -> R.string.ui_copy_sending_cf765512
    "Sent file" -> R.string.ui_copy_sent_file_f49a0823
    "Server folder" -> R.string.ui_copy_server_folder_fb1c1bc5
    "Session" -> R.string.ui_copy_session_f7f1997c
    "Session token (optional)" -> R.string.ui_copy_session_token_optional_2a8a2056
    "Set up Turp" -> R.string.ui_copy_set_up_turp_f554f170
    "Set up a provider" -> R.string.ui_copy_set_up_a_provider_a3a52a95
    "Set up a provider to start" -> R.string.ui_copy_set_up_a_provider_to_start_0994c6b5
    "Set up provider" -> R.string.ui_copy_set_up_provider_f899c153
    "Settings" -> R.string.ui_copy_settings_c7f73bb5
    "Settings stay optional" -> R.string.ui_copy_settings_stay_optional_5801a33a
    "Setup" -> R.string.ui_copy_setup_cdd7bb28
    "Setup & connections" -> R.string.ui_copy_setup_connections_85bdfce5
    "Setup details" -> R.string.ui_copy_setup_details_97269512
    "Share" -> R.string.ui_copy_share_09ca55ca
    "Share Turp chat" -> R.string.ui_copy_share_turp_chat_2c99756b
    "Share image" -> R.string.ui_copy_share_image_ecb8ec64
    "Share message" -> R.string.ui_copy_share_message_c1c0e65f
    "Share portable chat" -> R.string.ui_copy_share_portable_chat_a106eb58
    "Short reasoning" -> R.string.ui_copy_short_reasoning_feefea5a
    "Show OCR overlay" -> R.string.ui_copy_show_ocr_overlay_977f1ec9
    "Show attempts" -> R.string.ui_copy_show_attempts_b64a5456
    "Show backups" -> R.string.ui_copy_show_backups_9f5fa28a
    "Show blur boundary guides" -> R.string.ui_copy_show_blur_boundary_guides_f77a0583
    "Show complete package plan" -> R.string.ui_copy_show_complete_package_plan_0184aedb
    "Show package plan" -> R.string.ui_copy_show_package_plan_41d0358c
    "Show performance overlay" -> R.string.ui_copy_show_performance_overlay_fde38582
    "Show source" -> R.string.ui_copy_show_source_b470a520
    "Show the full plan and wait for you" -> R.string.ui_copy_show_the_full_plan_and_wait_for_you_245c9516
    "Show tool diagnostics" -> R.string.ui_copy_show_tool_diagnostics_94a36edd
    "Shows live frame timing without forcing continuous animation. The monitor observes frames already rendered by Android." -> R.string.ui_copy_shows_live_frame_timing_without_forcing_co_0b2fd382
    "Shows raw tool inputs, outputs, source paths, and copyable failure diagnostics inside Working." -> R.string.ui_copy_shows_raw_tool_inputs_outputs_source_paths_34892969
    "Sign in" -> R.string.ui_copy_sign_in_ada2e9e9
    "Sign in again" -> R.string.ui_copy_sign_in_again_7842ff56
    "Sign in with ChatGPT" -> R.string.ui_copy_sign_in_with_chatgpt_fe0b3b61
    "Sign in without pasting an API key." -> R.string.ui_copy_sign_in_without_pasting_an_api_key_6acc0e4c
    "Sign in; Turp only uses its app folder" -> R.string.ui_copy_sign_in_turp_only_uses_its_app_folder_1177a8cd
    "Sign out" -> R.string.ui_copy_sign_out_dc1649a1
    "Signing SHA-1" -> R.string.ui_copy_signing_sha_1_c7a274c3
    "Skip for now" -> R.string.ui_copy_skip_for_now_6fc09607
    "Softens the boundary where flat panels merge into the page." -> R.string.ui_copy_softens_the_boundary_where_flat_panels_mer_1f3c2110
    "Source" -> R.string.ui_copy_source_6da13add
    "Source commit" -> R.string.ui_copy_source_commit_0771cbb2
    "Source failed" -> R.string.ui_copy_source_failed_057918be
    "Source read" -> R.string.ui_copy_source_read_e6a01b31
    "Source read completed" -> R.string.ui_copy_source_read_completed_54656ea1
    "Source repository" -> R.string.ui_copy_source_repository_9110973f
    "Source request ready" -> R.string.ui_copy_source_request_ready_373a3588
    "Sources" -> R.string.ui_copy_sources_2eb56be3
    "Start a response" -> R.string.ui_copy_start_a_response_ba37e236
    "Start setup" -> R.string.ui_copy_start_setup_ecde253f
    "Starting fresh? Ignore the restore card and tap Continue." -> R.string.ui_copy_starting_fresh_ignore_the_restore_card_and_353907b8
    "Starting package installation" -> R.string.ui_copy_starting_package_installation_2ed2a891
    "Starting state for the controls beside the message box." -> R.string.ui_copy_starting_state_for_the_controls_beside_the_c302c89a
    "Steer current response" -> R.string.ui_copy_steer_current_response_cfe79dea
    "Steered by user" -> R.string.ui_copy_steered_by_user_ba08107d
    "Stop" -> R.string.ui_copy_stop_9e253470
    "Stop current response" -> R.string.ui_copy_stop_current_response_4b9a664e
    "Stop image generation" -> R.string.ui_copy_stop_image_generation_c29d03dd
    "Stopped" -> R.string.ui_copy_stopped_51e9111b
    "Stopped by user" -> R.string.ui_copy_stopped_by_user_a585e479
    "Streaming…" -> R.string.ui_copy_streaming_a37cc13b
    "Sunset" -> R.string.ui_copy_sunset_181bcd57
    "Switch account" -> R.string.ui_copy_switch_account_a28b0233
    "Switched branch" -> R.string.ui_copy_switched_branch_dea3f0d5
    "Switched to another branch" -> R.string.ui_copy_switched_to_another_branch_d2a3d3cd
    "System prompt" -> R.string.ui_copy_system_prompt_1e8970b6
    "Take a photo and attach it" -> R.string.ui_copy_take_a_photo_and_attach_it_2d7917b6
    "Take a photo to use as a reference" -> R.string.ui_copy_take_a_photo_to_use_as_a_reference_cbcac553
    "Tap a result to add it to the install list" -> R.string.ui_copy_tap_a_result_to_add_it_to_the_install_list_63a0ffcb
    "Tap to zoom" -> R.string.ui_copy_tap_to_zoom_46043b71
    "Target Android" -> R.string.ui_copy_target_android_b5826359
    "Terminal" -> R.string.ui_copy_terminal_a1f52cdc
    "Terminal, native CLI tools, and Linux packages" -> R.string.ui_copy_terminal_native_cli_tools_and_linux_packag_187201a0
    "Terms" -> R.string.ui_copy_terms_a55a275a
    "Terms & disclaimer" -> R.string.ui_copy_terms_disclaimer_7add8494
    "Text opacity" -> R.string.ui_copy_text_opacity_83e09703
    "The Android document picker can save directly to Google Drive, OneDrive, Dropbox, Nextcloud, a USB drive, or local storage. Turp does not upload through a hidden server." -> R.string.ui_copy_the_android_document_picker_can_save_direc_92e0305e
    "The Turp maintainer does not create, train, host, pre-review, or endorse individual model outputs. AI output can be wrong, unsafe, biased, or unsuitable; verify it before relying on it. Provider terms, fees, retention, and content rules apply independently." -> R.string.ui_copy_the_turp_maintainer_does_not_create_trai_448f39da
    "The built-in provider catalog is delayed. Setup remains usable and Turp will keep retrying in the background." -> R.string.ui_copy_the_built_in_provider_catalog_is_delayed_s_ce0dd5b5
    "The edited source is compiled, executed in the bounded runtime, and rendered before use." -> R.string.ui_copy_the_edited_source_is_compiled_executed_in__476436cf
    "The embedded license document could not be opened." -> R.string.ui_copy_the_embedded_license_document_could_not_be_74bfeeea
    "The exact prompt bundled with this app version is shown below. It is selectable for inspection and intentionally read-only." -> R.string.ui_copy_the_exact_prompt_bundled_with_this_app_ver_f8633265
    "The instance must enable JSON search output." -> R.string.ui_copy_the_instance_must_enable_json_search_outpu_f0ac141c
    "The overlay explicitly shares pointer input with the content underneath and never consumes it. Taps, scrolling, drawer gestures, and back navigation continue through the panel." -> R.string.ui_copy_the_overlay_explicitly_shares_pointer_inpu_361c70b9
    "The previous install failed. Review before retrying." -> R.string.ui_copy_the_previous_install_failed_review_before__8358b9cf
    "The previous install was interrupted. Review before retrying." -> R.string.ui_copy_the_previous_install_was_interrupted_revie_2a736282
    "The provider exposed the query but did not return result metadata or citations." -> R.string.ui_copy_the_provider_exposed_the_query_but_did_not_a3c03ab1
    "The provider returned no models" -> R.string.ui_copy_the_provider_returned_no_models_6006d7a4
    "The provider stream failed without returning additional diagnostic text." -> R.string.ui_copy_the_provider_stream_failed_without_returni_b8fc8331
    "The response stopped before it completed." -> R.string.ui_copy_the_response_stopped_before_it_completed_7faf04cb
    "The reviewed plan is no longer available. Run preflight again." -> R.string.ui_copy_the_reviewed_plan_is_no_longer_available_r_dd501c04
    "The source and conversation are intact." -> R.string.ui_copy_the_source_and_conversation_are_intact_e8bc1257
    "Theme mode" -> R.string.ui_copy_theme_mode_a4085941
    "Theme, palette, launcher icon, and AMOLED black" -> R.string.ui_copy_theme_palette_launcher_icon_and_amoled_bla_d2a156c2
    "These providers keep Turp backups in an app-specific folder or prefix. OAuth tokens and storage credentials are encrypted on this device and excluded from exported backups." -> R.string.ui_copy_these_providers_keep_turp_backups_in_an__1d48776c
    "Thinking" -> R.string.ui_copy_thinking_d08d8da0
    "Thinking always on" -> R.string.ui_copy_thinking_always_on_cfd10cca
    "Thinking effort" -> R.string.ui_copy_thinking_effort_ffa4386d
    "Third-party AI and services" -> R.string.ui_copy_third_party_ai_and_services_2efc45f4
    "This PDF could not be rendered" -> R.string.ui_copy_this_pdf_could_not_be_rendered_02eed081
    "This attempt continued in the newer step below." -> R.string.ui_copy_this_attempt_continued_in_the_newer_step_b_be3455d4
    "This build has no GitHub source provenance" -> R.string.ui_copy_this_build_has_no_github_source_provenance_5c6bd0b4
    "This build is missing its generated offline notices." -> R.string.ui_copy_this_build_is_missing_its_generated_offlin_04e7a509
    "This file is encrypted. Enter its password to inspect it." -> R.string.ui_copy_this_file_is_encrypted_enter_its_password__a264ac25
    "This is a debug-signed development build." -> R.string.ui_copy_this_is_a_debug_signed_development_build_a41215a2
    "This location is connected, but it does not contain a Turp backup yet." -> R.string.ui_copy_this_location_is_connected_but_it_does_not_43aa5057
    "This model cannot read the original attachment. OCR fallback will be prepared before sending." -> R.string.ui_copy_this_model_cannot_read_the_original_attach_207f386d
    "This model does not accept reference images." -> R.string.ui_copy_this_model_does_not_accept_reference_image_4ea7de09
    "This model doesn't support tool calling. Web, Python, and Linux tools won't run." -> R.string.ui_copy_this_model_doesn_t_support_tool_calling_we_1b99daec
    "This model receives OCR text and coordinates; you still see the untouched original." -> R.string.ui_copy_this_model_receives_ocr_text_and_coordinat_9c63ece0
    "This permanently removes the selected memory data from Turp." -> R.string.ui_copy_this_permanently_removes_the_selected_memo_014d9f87
    "This provider returns the final image when generation finishes." -> R.string.ui_copy_this_provider_returns_the_final_image_when_bb8b2c86
    "This removes the selected Linux root filesystem and its installed packages for all chats. Chat files in /workspace and bundled Python packages are kept." -> R.string.ui_copy_this_removes_the_selected_linux_root_files_1834b47d
    "This saved package request already completed successfully." -> R.string.ui_copy_this_saved_package_request_already_complet_dcaa5d0f
    "Timed out" -> R.string.ui_copy_timed_out_edcd3630
    "Tint is fully opaque and covers the blurred background. Lower Tint opacity to reveal blur." -> R.string.ui_copy_tint_is_fully_opaque_and_covers_the_blurre_b79c4db7
    "Tint opacity" -> R.string.ui_copy_tint_opacity_96fb97b9
    "Token counting" -> R.string.ui_copy_token_counting_a4f106cd
    "Token fields are stored from provider usage metadata when the provider reports them. Otherwise Turp may fall back to its local counter/estimator. Use the raw token counts above with your provider's current pricing table for manual calculation." -> R.string.ui_copy_token_fields_are_stored_from_provider_usag_cd3b8407
    "Tool behavior" -> R.string.ui_copy_tool_behavior_e85448db
    "Tool call" -> R.string.ui_copy_tool_call_e313188e
    "Tool completed" -> R.string.ui_copy_tool_completed_3ac043d2
    "Tool defaults" -> R.string.ui_copy_tool_defaults_ec2f1d87
    "Tool diagnostics" -> R.string.ui_copy_tool_diagnostics_983e902f
    "Tool failed" -> R.string.ui_copy_tool_failed_34ded43e
    "Tool output" -> R.string.ui_copy_tool_output_363f4fd0
    "Tool result" -> R.string.ui_copy_tool_result_c6a723b9
    "Tool traces" -> R.string.ui_copy_tool_traces_604ae021
    "Tool traces and working timeline" -> R.string.ui_copy_tool_traces_and_working_timeline_e020966a
    "Tools" -> R.string.ui_copy_tools_4fa8cc86
    "Tools & safety" -> R.string.ui_copy_tools_safety_b9da3713
    "Tools and modes" -> R.string.ui_copy_tools_and_modes_128b5854
    "Tools available to Turp in this chat" -> R.string.ui_copy_tools_available_to_turp_in_this_chat_3fc79d3d
    "Top left" -> R.string.ui_copy_top_left_04b38cf9
    "Top right" -> R.string.ui_copy_top_right_c1cc898f
    "Total tokens" -> R.string.ui_copy_total_tokens_e6dad16e
    "Trusted Linux packages (apt/apk)" -> R.string.ui_copy_trusted_linux_packages_apt_apk_3997a99f
    "Trusted list" -> R.string.ui_copy_trusted_list_beb86078
    "Trusted pip packages" -> R.string.ui_copy_trusted_pip_packages_3746e779
    "Try a different word or a shorter phrase." -> R.string.ui_copy_try_a_different_word_or_a_shorter_phrase_fd120b75
    "Try a library name such as SQLCipher or a license such as Apache-2.0." -> R.string.ui_copy_try_a_library_name_such_as_sqlcipher_or_a__42aa42fd
    "Try again" -> R.string.ui_copy_try_again_042c862e
    "Try another provider or clear the current filters." -> R.string.ui_copy_try_another_provider_or_clear_the_current__19f68da0
    "Try full rendering" -> R.string.ui_copy_try_full_rendering_80679aa3
    "Type a message or attach a file first" -> R.string.ui_copy_type_a_message_or_attach_a_file_first_112be641
    "UI draw / recording" -> R.string.ui_copy_ui_draw_recording_7d905e40
    "Ubuntu result" -> R.string.ui_copy_ubuntu_result_cecb1775
    "Ubuntu tool result" -> R.string.ui_copy_ubuntu_tool_result_c84c0cbf
    "Unarchive" -> R.string.ui_copy_unarchive_35e71c29
    "Unavailable / calculate manually" -> R.string.ui_copy_unavailable_calculate_manually_8a46a938
    "Unavailable in this build • tap for details" -> R.string.ui_copy_unavailable_in_this_build_tap_for_details_3f046a0d
    "Uninstall" -> R.string.ui_copy_uninstall_a735da1d
    "Unknown" -> R.string.ui_copy_unknown_bc7819b3
    "Unlock preview" -> R.string.ui_copy_unlock_preview_5e1981a6
    "Unpin" -> R.string.ui_copy_unpin_2eba6a03
    "Update check failed" -> R.string.ui_copy_update_check_failed_1baabb78
    "Update interval" -> R.string.ui_copy_update_interval_7c0e8a38
    "Updates" -> R.string.ui_copy_updates_c76d1807
    "Usage" -> R.string.ui_copy_usage_0bb18642
    "Usage & limits" -> R.string.ui_copy_usage_limits_199e5478
    "Usage details" -> R.string.ui_copy_usage_details_e3d72694
    "Use OpenAI, Anthropic, Gemini, DeepSeek, or another compatible endpoint." -> R.string.ui_copy_use_openai_anthropic_gemini_deepseek_or_an_dbc45081
    "Use Turp default for new chats" -> R.string.ui_copy_use_turp_default_for_new_chats_7d65475a
    "Use a ChatGPT account, API provider, or local server." -> R.string.ui_copy_use_a_chatgpt_account_api_provider_or_loca_453052ab
    "Use a key restricted to this bucket and prefix. Credentials remain encrypted on-device." -> R.string.ui_copy_use_a_key_restricted_to_this_bucket_and_pr_2fe03688
    "Use chart JSON or `label: value` lines." -> R.string.ui_copy_use_chart_json_or_label_value_lines_f2a909d0
    "Use connected folder" -> R.string.ui_copy_use_connected_folder_90486d5d
    "Use custom instruction profiles for additional tone and workflow preferences." -> R.string.ui_copy_use_custom_instruction_profiles_for_additi_5ce55af1
    "Use for new chats" -> R.string.ui_copy_use_for_new_chats_2e661cc8
    "Use memory" -> R.string.ui_copy_use_memory_1bd1aeeb
    "Use selected model" -> R.string.ui_copy_use_selected_model_0c8ec874
    "Use terms, third-party AI limits, warranty, and liability" -> R.string.ui_copy_use_terms_third_party_ai_limits_warranty_a_5b45ab19
    "Use your ChatGPT plan without an API key" -> R.string.ui_copy_use_your_chatgpt_plan_without_an_api_key_bee8ee5d
    "Uses Dropbox App folder access and scoped file permissions. Turp cannot browse the rest of Dropbox." -> R.string.ui_copy_uses_dropbox_app_folder_access_and_scoped__e108dc0b
    "Uses OneDrive's Apps/Turp folder with Files.ReadWrite.AppFolder instead of access to the whole drive." -> R.string.ui_copy_uses_onedrive_s_apps_turp_folder_with_fi_86f5b49b
    "Uses only Drive's hidden appDataFolder. Connect once, then create, browse, preview, and restore backups without repeating the consent flow." -> R.string.ui_copy_uses_only_drive_s_hidden_appdatafolder_con_119f5820
    "Usually unnecessary" -> R.string.ui_copy_usually_unnecessary_71831aa8
    "Verify" -> R.string.ui_copy_verify_dda6ac27
    "Version" -> R.string.ui_copy_version_2da600bf
    "Version, architecture, and privacy model" -> R.string.ui_copy_version_architecture_and_privacy_model_5a32ad48
    "Violet" -> R.string.ui_copy_violet_ddcd2f2e
    "Vision" -> R.string.ui_copy_vision_40b0906c
    "Waiting for Google Drive approval…" -> R.string.ui_copy_waiting_for_google_drive_approval_6ede8f2c
    "Waiting for output…" -> R.string.ui_copy_waiting_for_output_c0bc6805
    "Waiting for search results…" -> R.string.ui_copy_waiting_for_search_results_c3fcd815
    "Waiting for sign-in to finish" -> R.string.ui_copy_waiting_for_sign_in_to_finish_c6fa0ae2
    "Waiting for the first provider-rendered preview…" -> R.string.ui_copy_waiting_for_the_first_provider_rendered_pr_b9f66ed7
    "Warm orange, rose, and gold accents" -> R.string.ui_copy_warm_orange_rose_and_gold_accents_202bf19b
    "Web page" -> R.string.ui_copy_web_page_154cf598
    "Web search" -> R.string.ui_copy_web_search_381ceddd
    "WebDAV / Nextcloud" -> R.string.ui_copy_webdav_nextcloud_61e55664
    "WebDAV folder URL" -> R.string.ui_copy_webdav_folder_url_046e80a2
    "Weekly" -> R.string.ui_copy_weekly_158f3da5
    "Weekly limit" -> R.string.ui_copy_weekly_limit_7aaf676b
    "Welcome" -> R.string.ui_copy_welcome_ca4f9dcf
    "What are we creating?" -> R.string.ui_copy_what_are_we_creating_20d7e61f
    "What are we working on?" -> R.string.ui_copy_what_are_we_working_on_81ac7f3a
    "What this screen manages" -> R.string.ui_copy_what_this_screen_manages_542d8dc3
    "While Turp is working" -> R.string.ui_copy_while_turp_is_working_34273e03
    "While working" -> R.string.ui_copy_while_working_3cf6d7a9
    "Within frame budget" -> R.string.ui_copy_within_frame_budget_b9f948c7
    "Work complete" -> R.string.ui_copy_work_complete_272c4c9f
    "Working" -> R.string.ui_copy_working_3b4dfc97
    "Working display" -> R.string.ui_copy_working_display_eb79a380
    "Working history token budget" -> R.string.ui_copy_working_history_token_budget_4f0829dc
    "Working-history token budget" -> R.string.ui_copy_working_history_token_budget_98c02156
    "Working…" -> R.string.ui_copy_working_13b7bfca
    "Works with AWS S3, Cloudflare R2, Backblaze B2, MinIO, and other Signature V4-compatible object stores." -> R.string.ui_copy_works_with_aws_s3_cloudflare_r2_backblaze__21bac943
    "Works with Drive, OneDrive, Dropbox, Nextcloud, USB, local storage, and other Android document providers." -> R.string.ui_copy_works_with_drive_onedrive_dropbox_nextclou_b4545839
    "Workspace" -> R.string.ui_copy_workspace_4ca0a75c
    "Writing report" -> R.string.ui_copy_writing_report_37d1a667
    "Writing source request…" -> R.string.ui_copy_writing_source_request_3021661d
    "Turp adds request-specific date, enabled-tool, research, memory, attachment, and generated-content instructions at runtime. Developer Options → System prompt laboratory can inspect and override those Turp-controlled layers and show their effective context. Provider/API-owned prompts that Turp never receives remain outside Turp's control." -> R.string.ui_copy_turp_adds_request_specific_date_enabled__388d9dd8
    "Turp asks for the non-sensitive drive.appdata scope only. The backup files remain hidden from normal Drive browsing and from other apps." -> R.string.ui_copy_turp_asks_for_the_non_sensitive_drive_ap_1514dc2b
    "Turp cannot send messages until ChatGPT, an API provider, or a local model server is connected." -> R.string.ui_copy_turp_cannot_send_messages_until_chatgpt__31d3179a
    "Turp chat" -> R.string.ui_copy_turp_chat_cb1f11e8
    "Turp cloud folder connected" -> R.string.ui_copy_turp_cloud_folder_connected_2e05f8b8
    "Turp core prompt" -> R.string.ui_copy_turp_core_prompt_768aad75
    "Turp includes each installed root filesystem, packages, and configuration. Permissions, symbolic links, and hard links are preserved. This can make the backup several gigabytes." -> R.string.ui_copy_turp_includes_each_installed_root_filesy_fcf873a0
    "Turp is a client, not an AI model host. Responses come from the provider or local server selected by the user." -> R.string.ui_copy_turp_is_a_client_not_an_ai_model_host_re_1e39bcf3
    "Turp is ready" -> R.string.ui_copy_turp_is_ready_13cb949f
    "Turp is up to date" -> R.string.ui_copy_turp_is_up_to_date_ee323ee0
    "Turp stores memories in its encrypted local database and selects only relevant items under a strict context budget. Disabled memories remain stored but are not supplied to models." -> R.string.ui_copy_turp_stores_memories_in_its_encrypted_lo_22af7bbe
    "Turp stream error" -> R.string.ui_copy_turp_stream_error_bc4660cd
    "cost unavailable" -> R.string.ui_copy_cost_unavailable_b1d105c1
    "not found" -> R.string.ui_copy_not_found_094b763b
    "running…" -> R.string.ui_copy_running_c85296cf
    "saved history" -> R.string.ui_copy_saved_history_b269f3ad
    else -> null
}
