package com.walhalla.compose.viewmodel

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.CloudBackupCoordinator
import com.walhalla.appextractor.ExtractorHelper
import com.walhalla.appextractor.interactors.SimpleMeta
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.model.ViewModel as ExtractorLineViewModel
import com.walhalla.appextractor.utils.ApkUtils
import com.walhalla.appextractor.utils.IconSaver
import com.walhalla.appextractor.utils.MarketUtils
import com.walhalla.appextractor.utils.XmlUtils
import com.walhalla.compose.R
import com.walhalla.compose.log.AppLogBus
import com.walhalla.ui.plugins.Launcher
import com.walhalla.ui.plugins.Module_U
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ExtractorViewModel(application: Application) : AndroidViewModel(application) {
    /** Full installed list — never shrink by search. */
    private val _allApps = MutableStateFlow<List<PackageMeta>>(emptyList())

    private val _apps = MutableStateFlow<List<PackageMeta>>(emptyList())
    val apps: StateFlow<List<PackageMeta>> = _apps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _extractionProgress = MutableStateFlow<Pair<String, Int>?>(null)
    val extractionProgress: StateFlow<Pair<String, Int>?> = _extractionProgress.asStateFlow()

    private val _extractionFileCount = MutableStateFlow<Pair<String, Int>?>(null)
    val extractionFileCount: StateFlow<Pair<String, Int>?> = _extractionFileCount.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    private val _selectedApps = MutableStateFlow<Set<PackageMeta>>(emptySet())
    val selectedApps: StateFlow<Set<PackageMeta>> = _selectedApps.asStateFlow()

    private val _showAboutDialog = MutableStateFlow(false)
    val showAboutDialog: StateFlow<Boolean> = _showAboutDialog.asStateFlow()

    /** Host Activity for Dropbox OAuth / dialogs — set from Compose screen. */
    var activityProvider: (() -> Activity?)? = null

    private var cloudBackup: CloudBackupCoordinator? = null

    private val extractorHelper = ExtractorHelper(
        mThreadExecutor = CoroutineScope(Dispatchers.Default),
        mMainThread = CoroutineScope(Dispatchers.Main),
        mView = object : ExtractorHelper.Callback {
            override fun rbcProgressCallback(
                position: Int,
                totalFileSize: Int,
                file: File,
                fileSize: String,
                progress: Double
            ) {
                _extractionProgress.value = file.name to progress.toInt()
                _extractionFileCount.value = file.name to totalFileSize
            }

            override fun failureExtracted(resId: Int) {
                val msg = getApplication<Application>().getString(resId)
                _successMessage.value = msg
                AppLogBus.error(msg)
            }

            override fun makeSnackBar(file: File) {
                val msg = "APK extracted to:\n${file.path}"
                _successMessage.value = msg
                AppLogBus.success(msg)
            }

            override fun printOutput(viewModel: ExtractorLineViewModel) {
                AppLogBus.success(viewModel.toString())
            }

            override fun successExtracted(mFile: Map<SimpleMeta, List<File>>) {
                val count = mFile.values.sumOf { it.size }
                val paths = mFile.values.flatten().joinToString("\n") { it.path }
                val msg = if (count == 1) {
                    "APK extracted to:\n$paths"
                } else {
                    "Extracted $count files:\n$paths"
                }
                _successMessage.value = msg
                AppLogBus.success(msg)
                runCloudBackup(mFile)
            }

            override fun makeStorageLocalProgressBar(totalFileSize: Int) {
                _extractionFileCount.value = "extract" to totalFileSize
            }

            override fun hideProgressBar(size: Int) {
                _extractionProgress.value = null
                _extractionFileCount.value = null
            }

            override fun extractOnePleaseDialog(noneExtracted: List<PackageMeta>) {
                val names = noneExtracted.joinToString { it.label }
                val msg = "Need root to extract: $names"
                _successMessage.value = msg
                AppLogBus.error(msg)
            }
        }
    )

    init {
        loadInstalledApps()
    }

    private fun runCloudBackup(listMap: Map<SimpleMeta, List<File>>) {
        val ctx = getApplication<Application>()
        cloudBackup?.cancel()
        cloudBackup = CloudBackupCoordinator(
            ctx,
            activityProvider?.invoke(),
            object : CloudBackupCoordinator.Listener {
                override fun onCloudMessage(success: Boolean, message: String) {
                    if (success) {
                        _successMessage.value = message
                        AppLogBus.success(message)
                    } else {
                        AppLogBus.error(message)
                    }
                }

                override fun onCloudProgress(progress: Int, file: File?, fileSize: String?) {
                    _extractionProgress.value = (file?.name ?: "cloud") to progress
                }

                override fun onCloudBusy(busy: Boolean) {
                    if (!busy) {
                        _extractionProgress.value = null
                    }
                }
            }
        )
        cloudBackup!!.handleSuccessExtracted(listMap)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applySearchFilter()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val context = getApplication<Application>()
                val pm = context.packageManager
                val packageInfos = pm.getInstalledPackages(PackageManager.GET_META_DATA)

                val appsList = packageInfos.map { packageInfo ->
                    val applicationInfo = packageInfo.applicationInfo ?: ApplicationInfo()
                    var hasSplits = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        hasSplits = applicationInfo.splitPublicSourceDirs != null &&
                                applicationInfo.splitPublicSourceDirs!!.isNotEmpty()
                    }

                    val pining = XmlUtils.isPinningEnabled(context, applicationInfo.packageName)


                    val meta = PackageMeta.Builder(applicationInfo.packageName)
                        .label(applicationInfo.loadLabel(pm).toString())
                        .setHasSplits(hasSplits)
                        .setHasPining(pining)
                        .setIsSystemApp((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
                        .setVersionCode(
                            if (ApkUtils.apiIsAtLeast(Build.VERSION_CODES.P))
                                packageInfo.longVersionCode else packageInfo.versionCode.toLong()
                        )
                        .setVersionName(packageInfo.versionName)
                        .setIcon(applicationInfo.icon)
                        .setInstallTime(packageInfo.firstInstallTime)
                        .setUpdateTime(packageInfo.lastUpdateTime)
                        .build()

                    val file = File(applicationInfo.publicSourceDir)
                    meta.fileSize = file.length()
                    meta.sourceDir = applicationInfo.sourceDir
                    meta.firstInstallTime = packageInfo.firstInstallTime
                    meta.lastUpdateTime = packageInfo.lastUpdateTime
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        meta.category = applicationInfo.category
                    }

                    meta
                }
                _allApps.value = appsList
                applySearchFilter()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Always filter from full [_allApps], never from already-filtered [_apps]. */
    private fun applySearchFilter() {
        val query = _searchQuery.value.trim()
        _apps.value = if (query.isEmpty()) {
            _allApps.value
        } else {
            _allApps.value.filter { app ->
                app.label.contains(query, ignoreCase = true) ||
                        app.packageName.contains(query, ignoreCase = true)
            }
        }
    }

    fun extractApkRequest(app: PackageMeta) {
        extractorHelper.executeInternal(listOf(app), getApplication())
    }

    fun extractSelectedApps() {
        val selected = _selectedApps.value.toList()
        if (selected.isEmpty()) return
        extractorHelper.executeInternal(selected, getApplication())
    }

    fun shareApk(app: PackageMeta) {
        // Extract first (via helper); share when snack reports the file — use source APK for share
        val context = getApplication<Application>()
        val src = File(app.sourceDir)
        if (!src.exists()) {
            extractApkRequest(app)
            return
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            src
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share APK").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun openPlayStore(app: PackageMeta) {
        val context = getApplication<Application>()
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "market://details?id=${app.packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Если Play Store не установлен, открываем в браузере
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://play.google.com/store/apps/details?id=${app.packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun launchApp(app: PackageMeta) {
        val context = getApplication<Application>()
        val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun uninstallApp(app: PackageMeta) {
        val context = getApplication<Application>()
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
            data = "package:${app.packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun copyPackageName(app: PackageMeta) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Package Name", app.packageName)
        clipboard.setPrimaryClip(clip)
    }


    fun saveIcon(app: PackageMeta) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = IconSaver.saveAppIcon(getApplication(), app)) {
                is IconSaver.Result.Success -> {
                    _successMessage.value = result.message
                    AppLogBus.success(result.message)
                }
                is IconSaver.Result.Error -> {
                    AppLogBus.error(result.message)
                }
            }
        }
    }

    fun openGooglePlayCategory(category: String) {
        MarketUtils.openGooglePlayCategory(getApplication(), category)
    }

    fun shareIcon(app: PackageMeta) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val drawable = context.packageManager.getApplicationIcon(app.packageName)
                val bitmap = drawable.toBitmap()

                val iconFile = File(context.cacheDir, "temp_icon.png")
                FileOutputStream(iconFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    iconFile
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(Intent.createChooser(intent, "Share Icon").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleAppSelection(app: PackageMeta) {
        val currentSelected = _selectedApps.value
        val newSelected = if (app in currentSelected) {
            currentSelected - app
        } else {
            currentSelected + app
        }
        println("DEBUG: Toggle selection for ${app.label}, was in selection: ${app in currentSelected}, new selection size: ${newSelected.size}")
        _selectedApps.value = newSelected
    }

    fun clearSelection() {
        _selectedApps.value = emptySet()
    }


    fun selectExtractedApps() {
        viewModelScope.launch {
            val extractedDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "ExtractedApks"
            )
            if (!extractedDir.exists()) return@launch

            val extractedFiles = extractedDir.listFiles { file -> file.extension == "apk" }
                ?.map { it.nameWithoutExtension }
                ?: return@launch

            _selectedApps.value = _allApps.value.filter { app ->
                extractedFiles.any { fileName ->
                    fileName.startsWith(app.label.replace(" ", "_"))
                }
            }.toSet()
        }
    }

    fun getAppByPackageName(packageName: String?): PackageMeta? {
        return _allApps.value.find { it.packageName == packageName }
    }

    fun shareApp() {
        val context = getApplication<Application>()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, context.getString(com.walhalla.extractor.R.string.app_name))
            putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(com.walhalla.extractor.R.string.action_share_app)))
    }

    fun discoverMoreApps() {
        val context = getApplication<Application>()
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://search?q=pub:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/developer?id=${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun rateApp() {
        val context = getApplication<Application>()
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun showAbout() {
        _showAboutDialog.value = true
    }

    fun hideAbout() {
        _showAboutDialog.value = false
    }

    fun getAppVersion(): String {
        return getApplication<Application>().packageManager.getPackageInfo(
            getApplication<Application>().packageName, 0
        ).versionName?:""
    }

    fun showPrivacyPolicy() {
        val context = getApplication<Application>()
        Launcher.openBrowser(context, context.getString(R.string.url_privacy_policy))
    }

    fun contactUs() {
        val context = getApplication<Application>()
        Module_U.feedback(context)
    }

    fun showAccessibility() {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
} 