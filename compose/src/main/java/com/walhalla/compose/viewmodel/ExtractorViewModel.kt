package com.walhalla.compose.viewmodel

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.ApkUtils
import com.walhalla.appextractor.utils.XmlUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.net.toUri
import com.walhalla.appextractor.utils.ExtractorUtils
import com.walhalla.appextractor.utils.FileUtil

class ExtractorViewModel(application: Application) : AndroidViewModel(application) {
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

    private val _selectedApps = MutableStateFlow<Set<PackageMeta>>(emptySet())
    val selectedApps: StateFlow<Set<PackageMeta>> = _selectedApps.asStateFlow()

    init {
        loadInstalledApps()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterApps()
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
                _apps.value = appsList
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun filterApps() {
        val query = _searchQuery.value
        if (query.isEmpty()) {
            loadInstalledApps()
            return
        }

        viewModelScope.launch {
            val filteredApps = _apps.value.filter { app ->
                app.label.contains(query, ignoreCase = true) ||
                        app.packageName.contains(query, ignoreCase = true)
            }
            _apps.value = filteredApps
        }
    }

    fun extractApk(app: PackageMeta): File? {
        println("DEBUG: Starting APK extraction for ${app.label}")
        try {
            val context = getApplication<Application>()
            
            // Get all APK files including splits
            val uniqueApkFiles = ExtractorUtils.getAllApkFilesForCurrentPackage(context, app.packageName)
            println("DEBUG: Found ${uniqueApkFiles.size} APK files to extract")

            // Show total file count
            _extractionFileCount.value = app.packageName to uniqueApkFiles.size
            println("DEBUG: Set extraction file count to ${uniqueApkFiles.size}")

            val apkDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "ExtractedApks"
            )
            if (!apkDir.exists()) {
                apkDir.mkdirs()
            }

            // Create package directory
            val packageDir = File(apkDir, app.packageName.replace(".", "_"))
            if (!packageDir.exists()) {
                packageDir.mkdirs()
            }

            val extractedFiles = mutableListOf<File>()
            var currentFileIndex = 0

            // Copy each APK file
            for (sourceFile in uniqueApkFiles) {
                if (!sourceFile.exists()) continue

                currentFileIndex++
                println("DEBUG: Extracting file ${currentFileIndex}/${uniqueApkFiles.size}: ${sourceFile.name}")
                val destFile = File(packageDir, sourceFile.name)
                FileInputStream(sourceFile).use { input ->
                    FileOutputStream(destFile).use { output ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        var totalBytes: Long = 0
                        val fileSize = sourceFile.length()

                        while (input.read(buffer).also { length = it } > 0) {
                            output.write(buffer, 0, length)
                            totalBytes += length
                            val progress = ((totalBytes.toDouble() / fileSize.toDouble()) * 100).toInt()
                            _extractionProgress.value = app.packageName to progress
                            println("DEBUG: Progress for ${sourceFile.name}: $progress%")
                        }
                    }
                }
                extractedFiles.add(destFile)
            }

            println("DEBUG: Extraction completed")
            _extractionProgress.value = null
            _extractionFileCount.value = null

            if (extractedFiles.isNotEmpty()) {
                val file = extractedFiles[0]
                val fileSize = FileUtil.getFileSizeMegaBytes(file)
                val message = "APK extracted to:\n${file.path}\n$fileSize"
                _successMessage.value = message
                return file
            }
            return null
        } catch (e: IOException) {
            println("DEBUG: Error extracting APK: ${e.message}")
            e.printStackTrace()
            _extractionProgress.value = null
            _extractionFileCount.value = null
            _successMessage.value = null
            return null
        }
    }

    fun shareApk(app: PackageMeta) {
        println("DEBUG: Sharing APK for ${app.label}")
        viewModelScope.launch {
            val apkFile = extractApk(app) ?: return@launch
            val context = getApplication<Application>()
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                apkFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(Intent.createChooser(intent, "Share APK").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
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
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val drawable = context.packageManager.getApplicationIcon(app.packageName)
                val bitmap = drawable.toBitmap()

                val iconDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "AppIcons"
                )
                if (!iconDir.exists()) {
                    iconDir.mkdirs()
                }

                val iconFile = File(iconDir, "${app.label}_icon.png")
                FileOutputStream(iconFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
                    "${context.packageName}.provider",
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

            _selectedApps.value = _apps.value.filter { app ->
                extractedFiles.any { fileName ->
                    fileName.startsWith(app.label.replace(" ", "_"))
                }
            }.toSet()
        }
    }

    fun getAppByPackageName(packageName: String?): PackageMeta? {
        return _apps.value.find { it.packageName == packageName }
    }
} 