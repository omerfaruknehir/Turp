package app.turp.chat

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import app.turp.chat.chat.ChatRepository
import app.turp.chat.chat.AuxiliaryModelService
import app.turp.chat.agent.AgentTools
import app.turp.chat.agent.WebSearchClient
import app.turp.chat.data.TurpDatabase
import app.turp.chat.data.DefaultCatalog
import app.turp.chat.data.ProviderKind
import app.turp.chat.demo.DemoModeController
import app.turp.chat.files.AttachmentStore
import app.turp.chat.files.OcrEngine
import app.turp.chat.generation.GenerationScheduler
import app.turp.chat.provider.ProviderRegistry
import app.turp.chat.provider.AlibabaCloudModelDiscoveryService
import app.turp.chat.provider.AlibabaCloudModelPolicy
import app.turp.chat.provider.ModelRequestPolicy
import app.turp.chat.provider.HybridTokenCounter
import app.turp.chat.provider.OpenAiOAuthManager
import app.turp.chat.provider.OpenCodeManager
import app.turp.chat.provider.OpenRouterManager
import app.turp.chat.sandbox.PythonSandbox
import app.turp.chat.sandbox.UbuntuRuntime
import app.turp.chat.sandbox.PackageApprovalService
import app.turp.chat.sandbox.RunRecordStore
import app.turp.chat.security.SecureStore
import app.turp.chat.security.CrashReporter
import app.turp.chat.settings.AppPreferences
import app.turp.chat.settings.ComposerDraftStore
import app.turp.chat.settings.PersistentUiStateStore
import app.turp.chat.transfer.AppSettingsArchiveStore
import app.turp.chat.transfer.TurpArchiveManager
import app.turp.chat.transfer.GoogleDriveAppDataClient
import app.turp.chat.transfer.CloudOAuthManager
import app.turp.chat.transfer.DirectCloudConfigStore
import app.turp.chat.transfer.DirectCloudBackupCoordinator
import app.turp.chat.transfer.LinuxEnvironmentArchiveStore
import app.turp.chat.transfer.ScopedCloudFolderStore
import app.turp.chat.update.RepositoryUpdateManager
import app.turp.chat.generated.GeneratedBlockCompiler
import app.turp.chat.generated.GeneratedBlockRepairCoordinator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class CatalogInitializationState { LOADING, READY, FAILED }

class TurpApplication : Application() {
    private var launcherIconProcess = false

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        launcherIconProcess = isLauncherIconProcess()
        if (launcherIconProcess) return
        val crashReporter = CrashReporter(this).also(CrashReporter::install)
        container = AppContainer(this, crashReporter)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
            // Generated/returned assistant images are already known to the agent which created them.
            // Keep old installs visually clean instead of retaining an unnecessary OCR overlay.
            container.database.attachmentDao().clearAssistantImageAnalysis()
            // Builds before 0.11 materialized every tap on New chat. Remove
            // only rows that never acquired a message or attachment.
            val catalogDao = container.database.catalogDao()
            catalogDao.insertProvidersIfMissing(DefaultCatalog.providers)
            catalogDao.insertModelsIfMissing(DefaultCatalog.models)
            // 0.19.4 could leave existing official image rows absent or classified as chat.
            // Repair only Turp-owned OpenAI image presets; user-defined models remain untouched.
            catalogDao.upsertModels(ModelRequestPolicy.officialOpenAiImageModels())
            // 0.24.10 stored Alibaba's intentionally sparse /models response verbatim. Repair
            // documented capabilities for the built-in Qwen Cloud rows immediately so an app
            // update does not require a manual refresh. Custom providers are corrected from
            // their actual Alibaba endpoint during discovery/request routing instead.
            val qwenMetadataRepairs = catalogDao.allModels().mapNotNull { existing ->
                if (!existing.providerId.equals("qwen-cloud", ignoreCase = true)) return@mapNotNull null
                val enriched = AlibabaCloudModelPolicy.correct(
                    ModelRequestPolicy.enrichQwenCloudStoredModel(existing),
                )
                if (enriched == existing) null else enriched.copy(metadataUpdatedAt = System.currentTimeMillis())
            }
            if (qwenMetadataRepairs.isNotEmpty()) catalogDao.upsertModels(qwenMetadataRepairs)
            container.repository.observeProviders().first()
                .filter { it.kind == ProviderKind.OPENAI_OAUTH }
                .forEach { oauthProvider ->
                    if (container.openAiOAuth.signedInAccountId(oauthProvider.id) != null) {
                        container.secureStore.setApiKey(oauthProvider.id, "oauth-session")
                        if (!oauthProvider.registered || oauthProvider.apiKeyRequired) {
                            container.repository.saveProvider(oauthProvider.copy(registered = true, apiKeyRequired = false))
                        }
                    } else if (container.secureStore.apiKey(oauthProvider.id).isNotBlank()) {
                        // Keep the provider entry so it can be reconnected; only discard the stale marker.
                        container.secureStore.setApiKey(oauthProvider.id, "")
                    }
                }
            container.database.automationSettingsDao().upsert(
                container.database.automationSettingsDao().get() ?: app.turp.chat.data.AutomationSettingsEntity(),
            )
            if (BuildConfig.DEBUG) container.demoMode.reconcileAtStartup()
            container.markCatalogReady()
            } catch (error: Throwable) {
                container.markCatalogFailed()
            }
        }
    }


    private fun isLauncherIconProcess(): Boolean {
        val processName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Application.getProcessName()
        } else {
            val pid = Process.myPid()
            getSystemService(Context.ACTIVITY_SERVICE)
                .let { it as? ActivityManager }
                ?.runningAppProcesses
                ?.firstOrNull { it.pid == pid }
                ?.processName
                .orEmpty()
        }
        return processName.endsWith(":launcher_icon")
    }
}

class AppContainer(val application: Application, val crashReporter: CrashReporter) {
    private val _catalogInitializationState = MutableStateFlow(CatalogInitializationState.LOADING)
    val catalogInitializationState: StateFlow<CatalogInitializationState> = _catalogInitializationState.asStateFlow()

    internal fun markCatalogReady() { _catalogInitializationState.value = CatalogInitializationState.READY }
    internal fun markCatalogFailed() { _catalogInitializationState.value = CatalogInitializationState.FAILED }

    val appPreferences = AppPreferences(application)
    val composerDrafts = ComposerDraftStore(application)
    val persistentUiState = PersistentUiStateStore(application)
    val secureStore = SecureStore(application)
    val database = TurpDatabase.create(application, secureStore.databasePassphrase())
    val repository = ChatRepository(database)
    val demoMode = DemoModeController(database, repository, appPreferences)
    val openAiOAuth = OpenAiOAuthManager(application, secureStore)
    val openCode = OpenCodeManager(secureStore)
    val openRouter = OpenRouterManager(secureStore)
    val providers = ProviderRegistry(openAiOAuth)
    val modelDiscovery = AlibabaCloudModelDiscoveryService(openAiOAuth)
    val tokenCounter = HybridTokenCounter()
    val auxiliaryModels = AuxiliaryModelService(repository, providers, secureStore)
    val attachmentStore = AttachmentStore(application, database.attachmentDao())
    val ocrEngine = OcrEngine(application, database.attachmentDao())
    val pythonSandbox = PythonSandbox(application)
    val ubuntuRuntime = UbuntuRuntime(application, pythonSandbox)
    val linuxEnvironmentArchives = LinuxEnvironmentArchiveStore(application, pythonSandbox, ubuntuRuntime)
    val appSettingsArchives = AppSettingsArchiveStore(application, appPreferences, database, secureStore)
    val archiveManager = TurpArchiveManager(application, database, linuxEnvironmentArchives, appSettingsArchives)
    val scopedCloudFolder = ScopedCloudFolderStore(application)
    val googleDriveAppData = GoogleDriveAppDataClient(application)
    val cloudOAuth = CloudOAuthManager(application, secureStore)
    val directCloudConfigs = DirectCloudConfigStore(secureStore)
    val directCloud = DirectCloudBackupCoordinator(application, cloudOAuth, directCloudConfigs)
    val repositoryUpdates = RepositoryUpdateManager(application)
    val runRecords = RunRecordStore(pythonSandbox::workspace)
    val generatedBlockCompiler = GeneratedBlockCompiler(application)
    val generatedBlockRepairs = GeneratedBlockRepairCoordinator(
        workspace = pythonSandbox::workspace,
        compileCandidate = generatedBlockCompiler::compile,
        requestRepair = auxiliaryModels::repairGeneratedBlock,
    )
    val packageApprovals = PackageApprovalService(repository, auxiliaryModels)
    val webSearchClient = WebSearchClient(appPreferences, secureStore)
    val agentTools = AgentTools(
        pythonSandbox,
        ubuntuRuntime,
        repository,
        generatedBlockCompiler,
        runRecords,
        webSearchClient,
    )
    val scheduler = GenerationScheduler(application, repository)
}
