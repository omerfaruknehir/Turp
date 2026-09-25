package app.turp.chat.update

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.edit
import app.turp.chat.BuildConfig
import app.turp.chat.installedAppVersion
import app.turp.chat.security.AppInstallIdentity
import app.turp.chat.security.currentAppInstallIdentity
import app.turp.chat.security.normalizeCertificateFingerprint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed interface RepositoryUpdateState {
    data object Unsupported : RepositoryUpdateState
    data object Idle : RepositoryUpdateState
    data object Checking : RepositoryUpdateState
    data class UpToDate(
        val latestVersion: String,
        val checkedAt: Long,
    ) : RepositoryUpdateState
    data class Ahead(
        val latestVersion: String,
        val checkedAt: Long,
    ) : RepositoryUpdateState
    data class Available(
        val release: RepositoryRelease,
        val checkedAt: Long,
    ) : RepositoryUpdateState
    data class Failed(
        val message: String,
        val checkedAt: Long,
    ) : RepositoryUpdateState
}

sealed interface InstalledReleaseNotesState {
    data object Hidden : InstalledReleaseNotesState
    data object Loading : InstalledReleaseNotesState
    data class Ready(val release: RepositoryRelease) : InstalledReleaseNotesState
    data class Failed(
        val versionName: String,
        val message: String,
        val releasePageUrl: String,
    ) : InstalledReleaseNotesState
}

data class RepositoryRelease(
    val repository: String,
    val tagName: String,
    val versionName: String,
    val versionCode: Int?,
    val releasePageUrl: String,
    val apkDownloadUrl: String?,
    val publishedAt: String?,
    val notes: String,
    val directInstallCompatible: Boolean,
    val compatibilityMessage: String?,
)

internal data class RepositoryReleaseAsset(
    val name: String,
    val downloadUrl: String,
)

internal data class RepositoryReleaseManifest(
    val repository: String,
    val tag: String,
    val versionName: String,
    val versionCode: Int,
    val packageName: String,
    val apkAsset: String,
    val apkSha256: String,
    val signingCertificateSha256: String,
    val sourceCommit: String,
)

class RepositoryUpdateManager(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private val installedVersion = appContext.installedAppVersion()
    private val repository = normalizeGitHubRepository(BuildConfig.SOURCE_REPOSITORY)
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(25, TimeUnit.SECONDS)
        .build()
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private val _state = MutableStateFlow<RepositoryUpdateState>(
        if (repository == null) RepositoryUpdateState.Unsupported else RepositoryUpdateState.Idle,
    )
    val state: StateFlow<RepositoryUpdateState> = _state.asStateFlow()
    private val _installedReleaseNotesState = MutableStateFlow<InstalledReleaseNotesState>(
        InstalledReleaseNotesState.Hidden,
    )
    val installedReleaseNotesState: StateFlow<InstalledReleaseNotesState> =
        _installedReleaseNotesState.asStateFlow()

    suspend fun checkIfDue(now: Long = System.currentTimeMillis()) {
        if (repository == null) return
        val lastAttempt = preferences.getLong(KEY_LAST_ATTEMPT, 0L)
        if (now - lastAttempt < AUTO_CHECK_INTERVAL_MILLIS) return
        check(now)
    }

    suspend fun check(now: Long = System.currentTimeMillis()) {
        val source = repository ?: run {
            _state.value = RepositoryUpdateState.Unsupported
            return
        }
        mutex.withLock {
            _state.value = RepositoryUpdateState.Checking
            preferences.edit { putLong(KEY_LAST_ATTEMPT, now) }
            runCatching {
                withContext(Dispatchers.IO) { fetchLatestRelease(source) }
            }.onSuccess { release ->
                cacheRelease(release)
                preferences.edit { putLong(KEY_LAST_SUCCESS, now) }
                _state.value = when {
                    isRepositoryVersionNewer(
                        candidateVersion = release.versionName,
                        currentVersion = installedVersion.versionName,
                        candidateVersionCode = release.versionCode,
                        currentVersionCode = installedVersion.versionCode,
                    ) -> RepositoryUpdateState.Available(release, now)
                    isInstalledRepositoryVersionAhead(
                        installedVersion = installedVersion.versionName,
                        latestVersion = release.versionName,
                        installedVersionCode = installedVersion.versionCode,
                        latestVersionCode = release.versionCode,
                    ) -> RepositoryUpdateState.Ahead(release.versionName, now)
                    else -> RepositoryUpdateState.UpToDate(release.versionName, now)
                }
            }.onFailure { error ->
                _state.value = RepositoryUpdateState.Failed(
                    message = updateFailureMessage(error),
                    checkedAt = now,
                )
            }
        }
    }

    suspend fun loadInstalledReleaseNotesIfNeeded() {
        val source = repository ?: run {
            recordInstalledVersionSeen()
            _installedReleaseNotesState.value = InstalledReleaseNotesState.Hidden
            return
        }
        val lastSeen = preferences
            .takeIf { it.contains(KEY_LAST_SEEN_INSTALLED_VERSION_CODE) }
            ?.getInt(KEY_LAST_SEEN_INSTALLED_VERSION_CODE, installedVersion.versionCode)
        val shouldShow = shouldShowInstalledReleaseNotes(
            lastSeenVersionCode = lastSeen,
            currentVersionCode = installedVersion.versionCode,
            wasUpdatedInstall = wasPackageUpdated(),
            debugBuild = BuildConfig.DEBUG,
        )
        if (!shouldShow) {
            if (lastSeen != installedVersion.versionCode) recordInstalledVersionSeen()
            _installedReleaseNotesState.value = InstalledReleaseNotesState.Hidden
            return
        }

        _installedReleaseNotesState.value = InstalledReleaseNotesState.Loading
        mutex.withLock {
            cachedReleaseForInstalledVersion()?.let { cached ->
                _installedReleaseNotesState.value = InstalledReleaseNotesState.Ready(cached)
                return@withLock
            }
            runCatching {
                withContext(Dispatchers.IO) { fetchInstalledRelease(source) }
            }.onSuccess { release ->
                cacheRelease(release)
                _installedReleaseNotesState.value = InstalledReleaseNotesState.Ready(release)
            }.onFailure { error ->
                _installedReleaseNotesState.value = InstalledReleaseNotesState.Failed(
                    versionName = installedVersion.versionName,
                    message = updateFailureMessage(error),
                    releasePageUrl = installedReleasePageUrl(source),
                )
            }
        }
    }

    fun markInstalledReleaseNotesSeen() {
        recordInstalledVersionSeen()
        _installedReleaseNotesState.value = InstalledReleaseNotesState.Hidden
    }

    fun shouldPrompt(tagName: String): Boolean =
        preferences.getString(KEY_LAST_PROMPTED_TAG, null) != tagName

    fun markPrompted(tagName: String) {
        preferences.edit { putString(KEY_LAST_PROMPTED_TAG, tagName) }
    }

    private fun fetchLatestRelease(source: String): RepositoryRelease =
        fetchRelease(source, "https://api.github.com/repos/$source/releases/latest")

    private fun fetchInstalledRelease(source: String): RepositoryRelease {
        val tag = "v${installedVersion.versionName}"
        return fetchRelease(source, "https://api.github.com/repos/$source/releases/tags/$tag")
    }

    private fun fetchRelease(source: String, releaseUrl: String): RepositoryRelease {
        val root = requestJson(releaseUrl)
        if (root["draft"]?.jsonPrimitive?.booleanOrNull == true) {
            throw IOException("The repository release is still a draft.")
        }
        val tag = root["tag_name"]?.jsonPrimitive?.contentOrNull
            ?.takeIf(String::isNotBlank)
            ?: throw IOException("The repository returned a release without a tag.")
        val pageUrl = root["html_url"]?.jsonPrimitive?.contentOrNull
            ?.takeIf { it.startsWith("https://github.com/$source/releases/") }
            ?: "https://github.com/$source/releases/tag/$tag"
        val assets = root["assets"]?.jsonArray.orEmpty().mapNotNull { element ->
            val asset = element.jsonObject
            val name = asset["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val url = asset["browser_download_url"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            if (!url.startsWith("https://github.com/$source/releases/download/")) return@mapNotNull null
            RepositoryReleaseAsset(name, url)
        }
        val tagVersion = tag.removePrefix("v").removePrefix("V")
        val manifestAsset = assets.firstOrNull {
            it.name == "Turp-$tagVersion-release.json" || it.name.endsWith("-release.json")
        }
        val manifest = manifestAsset?.let { asset ->
            runCatching { parseManifest(requestJson(asset.downloadUrl)) }.getOrNull()
        }
        val versionName = manifest?.versionName?.ifBlank { tagVersion } ?: tagVersion
        val apk = selectRepositoryReleaseApk(assets, versionName, manifest?.apkAsset)
        val identity = appContext.currentAppInstallIdentity()
        val compatible = manifest?.let { isRepositoryReleaseInstallCompatible(it, identity) } ?: false
        val compatibilityMessage = when {
            manifest == null -> "This release has no signed update manifest; open its release page instead of installing directly."
            manifest.packageName != identity.packageName ->
                "The release package ${manifest.packageName} does not match installed package ${identity.packageName}."
            normalizeCertificateFingerprint(manifest.signingCertificateSha256) !=
                normalizeCertificateFingerprint(identity.signingSha256) ->
                "The release is signed by a different certificate, so Android cannot install it over this build."
            apk == null -> "The release does not contain an Android APK asset."
            else -> null
        }
        val githubNotes = root["body"]?.jsonPrimitive?.contentOrNull.orEmpty().take(MAX_NOTES_CHARS)
        val notes = localizedReleaseNotes(
            source = source,
            tag = tag,
            versionName = versionName,
            fallback = githubNotes,
        )
        return RepositoryRelease(
            repository = source,
            tagName = tag,
            versionName = versionName,
            versionCode = manifest?.versionCode,
            releasePageUrl = pageUrl,
            apkDownloadUrl = apk?.downloadUrl,
            publishedAt = root["published_at"]?.jsonPrimitive?.contentOrNull,
            notes = notes,
            directInstallCompatible = compatible && apk != null,
            compatibilityMessage = compatibilityMessage,
        )
    }

    private fun localizedReleaseNotes(
        source: String,
        tag: String,
        versionName: String,
        fallback: String,
    ): String {
        val language = appContext.resources.configuration.locales[0]?.language
        if (!language.equals("tr", ignoreCase = true)) return fallback
        val localizedUrl =
            "https://raw.githubusercontent.com/$source/$tag/docs/releases/tr/RELEASE_NOTES_$versionName.md"
        return runCatching { requestText(localizedUrl).trim() }
            .getOrNull()
            ?.takeIf(String::isNotBlank)
            ?.take(MAX_NOTES_CHARS)
            ?: fallback
    }

    private fun requestJson(url: String) =
        json.parseToJsonElement(requestText(url)).jsonObject

    private fun requestText(url: String): String = client.newCall(
        Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("User-Agent", "Turp/${installedVersion.versionName} (${appContext.packageName})")
            .build(),
    ).execute().use { response ->
        val body = response.body?.string().orEmpty()
        if (!response.isSuccessful) {
            val detail = runCatching {
                json.parseToJsonElement(body).jsonObject["message"]?.jsonPrimitive?.contentOrNull
            }.getOrNull()
            throw IOException(
                when (response.code) {
                    403 -> "GitHub refused the update check, usually because its anonymous rate limit was reached."
                    404 -> "No published release was found in $repository."
                    else -> detail ?: "GitHub update check failed with HTTP ${response.code}."
                },
            )
        }
        body
    }

    private fun parseManifest(root: kotlinx.serialization.json.JsonObject): RepositoryReleaseManifest =
        RepositoryReleaseManifest(
            repository = root["repository"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            tag = root["tag"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            versionName = root["versionName"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            versionCode = root["versionCode"]?.jsonPrimitive?.intOrNull ?: 0,
            packageName = root["packageName"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            apkAsset = root["apkAsset"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            apkSha256 = root["apkSha256"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            signingCertificateSha256 = root["signingCertificateSha256"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            sourceCommit = root["sourceCommit"]?.jsonPrimitive?.contentOrNull.orEmpty(),
        )

    private fun cacheRelease(release: RepositoryRelease) {
        preferences.edit {
            putString(KEY_CACHED_RELEASE_REPOSITORY, release.repository)
            putString(KEY_CACHED_RELEASE_TAG, release.tagName)
            putString(KEY_CACHED_RELEASE_VERSION_NAME, release.versionName)
            if (release.versionCode != null) putInt(KEY_CACHED_RELEASE_VERSION_CODE, release.versionCode)
            else remove(KEY_CACHED_RELEASE_VERSION_CODE)
            putString(KEY_CACHED_RELEASE_PAGE_URL, release.releasePageUrl)
            putString(KEY_CACHED_RELEASE_APK_URL, release.apkDownloadUrl)
            putString(KEY_CACHED_RELEASE_PUBLISHED_AT, release.publishedAt)
            putString(KEY_CACHED_RELEASE_NOTES, release.notes)
            putBoolean(KEY_CACHED_RELEASE_DIRECT_INSTALL, release.directInstallCompatible)
            putString(KEY_CACHED_RELEASE_COMPATIBILITY_MESSAGE, release.compatibilityMessage)
        }
    }

    private fun cachedReleaseForInstalledVersion(): RepositoryRelease? {
        val versionName = preferences.getString(KEY_CACHED_RELEASE_VERSION_NAME, null)
            ?.takeIf { it == installedVersion.versionName }
            ?: return null
        val notes = preferences.getString(KEY_CACHED_RELEASE_NOTES, null)
            ?.takeIf(String::isNotBlank)
            ?: return null
        val tag = preferences.getString(KEY_CACHED_RELEASE_TAG, null)
            ?.takeIf(String::isNotBlank)
            ?: "v$versionName"
        val source = preferences.getString(KEY_CACHED_RELEASE_REPOSITORY, null)
            ?.takeIf(String::isNotBlank)
            ?: repository
            ?: return null
        return RepositoryRelease(
            repository = source,
            tagName = tag,
            versionName = versionName,
            versionCode = if (preferences.contains(KEY_CACHED_RELEASE_VERSION_CODE)) {
                preferences.getInt(KEY_CACHED_RELEASE_VERSION_CODE, installedVersion.versionCode)
            } else null,
            releasePageUrl = preferences.getString(KEY_CACHED_RELEASE_PAGE_URL, null)
                ?.takeIf(String::isNotBlank)
                ?: "https://github.com/$source/releases/tag/$tag",
            apkDownloadUrl = preferences.getString(KEY_CACHED_RELEASE_APK_URL, null),
            publishedAt = preferences.getString(KEY_CACHED_RELEASE_PUBLISHED_AT, null),
            notes = notes,
            directInstallCompatible = preferences.getBoolean(KEY_CACHED_RELEASE_DIRECT_INSTALL, false),
            compatibilityMessage = preferences.getString(KEY_CACHED_RELEASE_COMPATIBILITY_MESSAGE, null),
        )
    }

    private fun recordInstalledVersionSeen() {
        preferences.edit {
            putInt(KEY_LAST_SEEN_INSTALLED_VERSION_CODE, installedVersion.versionCode)
            putString(KEY_LAST_SEEN_INSTALLED_VERSION_NAME, installedVersion.versionName)
        }
    }

    private fun wasPackageUpdated(): Boolean {
        val packageInfo = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                appContext.packageManager.getPackageInfo(
                    appContext.packageName,
                    PackageManager.PackageInfoFlags.of(0),
                )
            } else {
                @Suppress("DEPRECATION")
                appContext.packageManager.getPackageInfo(appContext.packageName, 0)
            }
        }.getOrNull() ?: return false
        return packageInfo.lastUpdateTime > packageInfo.firstInstallTime + PACKAGE_UPDATE_CLOCK_TOLERANCE_MILLIS
    }

    private fun installedReleasePageUrl(source: String): String =
        "https://github.com/$source/releases/tag/v${installedVersion.versionName}"

    companion object {
        private const val PREFERENCES = "turp_repository_updates"
        private const val KEY_LAST_ATTEMPT = "last_attempt"
        private const val KEY_LAST_SUCCESS = "last_success"
        private const val KEY_LAST_PROMPTED_TAG = "last_prompted_tag"
        private const val KEY_LAST_SEEN_INSTALLED_VERSION_CODE = "last_seen_installed_version_code"
        private const val KEY_LAST_SEEN_INSTALLED_VERSION_NAME = "last_seen_installed_version_name"
        private const val KEY_CACHED_RELEASE_REPOSITORY = "cached_release_repository"
        private const val KEY_CACHED_RELEASE_TAG = "cached_release_tag"
        private const val KEY_CACHED_RELEASE_VERSION_NAME = "cached_release_version_name"
        private const val KEY_CACHED_RELEASE_VERSION_CODE = "cached_release_version_code"
        private const val KEY_CACHED_RELEASE_PAGE_URL = "cached_release_page_url"
        private const val KEY_CACHED_RELEASE_APK_URL = "cached_release_apk_url"
        private const val KEY_CACHED_RELEASE_PUBLISHED_AT = "cached_release_published_at"
        private const val KEY_CACHED_RELEASE_NOTES = "cached_release_notes"
        private const val KEY_CACHED_RELEASE_DIRECT_INSTALL = "cached_release_direct_install"
        private const val KEY_CACHED_RELEASE_COMPATIBILITY_MESSAGE = "cached_release_compatibility_message"
        private const val AUTO_CHECK_INTERVAL_MILLIS = 24L * 60L * 60L * 1_000L
        private const val PACKAGE_UPDATE_CLOCK_TOLERANCE_MILLIS = 1_500L
        private const val MAX_NOTES_CHARS = 4_000
    }
}

internal fun shouldShowInstalledReleaseNotes(
    lastSeenVersionCode: Int?,
    currentVersionCode: Int,
    wasUpdatedInstall: Boolean,
    debugBuild: Boolean = false,
): Boolean {
    if (debugBuild) return false
    return when {
        lastSeenVersionCode == null -> wasUpdatedInstall
        lastSeenVersionCode >= currentVersionCode -> false
        else -> true
    }
}

internal fun normalizeGitHubRepository(raw: String?): String? {
    val value = raw?.trim().orEmpty()
    if (value.isBlank()) return null
    val direct = Regex("^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$")
    if (direct.matches(value)) return value.removeSuffix(".git")
    return Regex("github\\.com[:/]([A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+?)(?:\\.git)?(?:[/?#].*)?$")
        .find(value)
        ?.groupValues
        ?.getOrNull(1)
        ?.removeSuffix(".git")
}

internal fun selectRepositoryReleaseApk(
    assets: List<RepositoryReleaseAsset>,
    versionName: String,
    manifestAssetName: String? = null,
): RepositoryReleaseAsset? {
    if (!manifestAssetName.isNullOrBlank()) {
        assets.firstOrNull { it.name == manifestAssetName }?.let { return it }
    }
    return assets.firstOrNull { it.name == "Turp-$versionName-release.apk" }
        ?: assets.firstOrNull { it.name.endsWith("-release.apk", ignoreCase = true) }
        ?: assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
}

internal fun isRepositoryReleaseInstallCompatible(
    manifest: RepositoryReleaseManifest,
    identity: AppInstallIdentity,
): Boolean =
    manifest.packageName == identity.packageName &&
        normalizeCertificateFingerprint(manifest.signingCertificateSha256) ==
        normalizeCertificateFingerprint(identity.signingSha256) &&
        manifest.apkAsset.endsWith(".apk", ignoreCase = true)

internal fun isRepositoryVersionNewer(
    candidateVersion: String,
    currentVersion: String,
    candidateVersionCode: Int? = null,
    currentVersionCode: Int? = null,
): Boolean {
    if (candidateVersionCode != null && currentVersionCode != null && candidateVersionCode != currentVersionCode) {
        return candidateVersionCode > currentVersionCode
    }
    return compareRepositoryVersions(candidateVersion, currentVersion) > 0
}

internal fun isInstalledRepositoryVersionAhead(
    installedVersion: String,
    latestVersion: String,
    installedVersionCode: Int? = null,
    latestVersionCode: Int? = null,
): Boolean = isRepositoryVersionNewer(
    candidateVersion = installedVersion,
    currentVersion = latestVersion,
    candidateVersionCode = installedVersionCode,
    currentVersionCode = latestVersionCode,
)

internal fun compareRepositoryVersions(left: String, right: String): Int {
    val leftParsed = parseRepositoryVersion(left)
    val rightParsed = parseRepositoryVersion(right)
    val count = maxOf(leftParsed.first.size, rightParsed.first.size)
    repeat(count) { index ->
        val result = (leftParsed.first.getOrNull(index) ?: 0)
            .compareTo(rightParsed.first.getOrNull(index) ?: 0)
        if (result != 0) return result
    }
    val leftSuffix = leftParsed.second
    val rightSuffix = rightParsed.second
    return when {
        leftSuffix == rightSuffix -> 0
        leftSuffix == null -> 1
        rightSuffix == null -> -1
        else -> leftSuffix.compareTo(rightSuffix, ignoreCase = true)
    }
}

private fun parseRepositoryVersion(value: String): Pair<List<Int>, String?> {
    val normalized = value.trim().removePrefix("v").removePrefix("V")
    val main = normalized.substringBefore('-').substringBefore('+')
    val suffix = normalized.substringAfter('-', "")
        .substringBefore('+')
        .takeIf(String::isNotBlank)
    val numbers = main.split('.').map { component ->
        component.takeWhile(Char::isDigit).toIntOrNull() ?: 0
    }
    return numbers to suffix
}

private fun updateFailureMessage(error: Throwable): String =
    error.message?.takeIf(String::isNotBlank) ?: "Could not check the source repository for updates."
