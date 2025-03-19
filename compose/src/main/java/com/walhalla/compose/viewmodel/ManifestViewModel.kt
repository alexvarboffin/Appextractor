package com.walhalla.compose.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.manifest.ManifestPresenterXml
import com.walhalla.appextractor.presenter.BaseManifestPresenter.ManifestCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.TreeSet
import android.os.Build
import kotlinx.coroutines.delay

data class ManifestState(
    val name: String,
    val content: String,
    val apkPath: String,
    var formattedContent: String? = null
)

class ManifestViewModel(application: Application) : AndroidViewModel(application), ManifestCallback {
    private val _manifestStates = MutableStateFlow<List<ManifestState>>(emptyList())
    val manifestStates: StateFlow<List<ManifestState>> = _manifestStates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPresenter: ManifestPresenterXml? = null
    private var currentApkPath: String? = null
    private var pendingState: ManifestState? = null

    private fun formatTabName(apkPath: String): String {
        val name = apkPath.split("/").last()
        return when {
            name.startsWith("split_config.") -> {
                // Для split APK показываем полное имя файла
                name
            }
            name == "base.apk" -> {
                // Для базового APK показываем как есть
                name
            }
            else -> name
        }
    }

    fun loadManifests(app: PackageMeta) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                println("DEBUG: Starting manifest load for ${app.packageName}")
                val apkPaths = getApkPaths(app.packageName)
                println("DEBUG: Found APK paths: $apkPaths")
                
                val states = withContext(Dispatchers.IO) {
                    apkPaths.mapNotNull { apkPath ->
                        try {
                            println("DEBUG: Processing APK: $apkPath")
                            currentApkPath = apkPath
                            val name = formatTabName(apkPath)

                            val presenter = ManifestPresenterXml(getApplication(), this@ManifestViewModel)
                            currentPresenter = presenter

                            if (!presenter.configForPackage(app.packageName, apkPath)) {
                                println("DEBUG: Failed to configure presenter for $apkPath")
                                return@mapNotNull null
                            }

                            println("DEBUG: Getting manifest content for $apkPath")
                            val content = presenter.mOutgetText(ManifestPresenterXml.ANDROID_MANIFEST_FILENAME)
                            println("DEBUG: Content length: ${content.length}")

                            // Создаем состояние и сохраняем его как ожидающее
                            val state = ManifestState(
                                name = name,
                                content = content,
                                apkPath = apkPath,
                                formattedContent = null
                            )
                            pendingState = state

                            println("DEBUG: Rendering XML for $apkPath")
                            presenter.renderXML(ManifestPresenterXml.ANDROID_MANIFEST_FILENAME)

                            // Ждем немного, чтобы получить отформатированный контент
                            var attempts = 0
                            while (pendingState?.formattedContent == null && attempts < 10) {
                                delay(100)
                                attempts++
                            }

                            pendingState?.also { 
                                println("DEBUG: State for $apkPath has formatted content: ${it.formattedContent != null}")
                            }
                            
                            pendingState
                        } catch (e: Exception) {
                            println("DEBUG: Error processing APK $apkPath: ${e.message}")
                            e.printStackTrace()
                            null
                        } finally {
                            pendingState = null
                        }
                    }
                }
                println("DEBUG: Final states count: ${states.size}")
                println("DEBUG: States with formatted content: ${states.count { it.formattedContent != null }}")
                _manifestStates.value = states
                if (states.isEmpty()) {
                    _error.value = "No manifests found"
                }
            } catch (e: Exception) {
                println("DEBUG: Fatal error: ${e.message}")
                e.printStackTrace()
                _error.value = e.message ?: "Failed to load manifests"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getApkPaths(packageName: String): Set<String> {
        val context = getApplication<Application>()
        val temp: MutableSet<String> = TreeSet()
        
        try {
            val applicationInfo = context.packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
            
            temp.add(applicationInfo.sourceDir)
            temp.add(applicationInfo.publicSourceDir)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                applicationInfo.splitPublicSourceDirs?.let {
                    temp.addAll(it)
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Package not found: $packageName", e)
        }
        
        return temp
    }

    override fun showError(text: String, t: Throwable?) {
        println("DEBUG: Error callback: $text, ${t?.message}")
        _error.value = "$text: ${t?.message ?: ""}"
    }

    override fun showManifestContent(s: String) {
        println("DEBUG: showManifestContent called with length: ${s.length}")
        val currentStates = _manifestStates.value.toMutableList()
        val currentPath = currentApkPath ?: return
        val index = currentStates.indexOfFirst { it.apkPath == currentPath }
        if (index != -1) {
            currentStates[index] = currentStates[index].copy(content = s)
            _manifestStates.value = currentStates
        }
    }

    override fun loadDataWithPatternHTML(encoded: String) {
        println("DEBUG: loadDataWithPatternHTML called with length: ${encoded.length}")
        
        // Сначала пробуем обновить ожидающее состояние
        pendingState?.let { state ->
            pendingState = state.copy(formattedContent = encoded)
            println("DEBUG: Updated pending state with formatted content")
            return
        }

        // Если нет ожидающего состояния, обновляем в списке
        val currentStates = _manifestStates.value.toMutableList()
        val currentPath = currentApkPath ?: return
        val index = currentStates.indexOfFirst { it.apkPath == currentPath }
        if (index != -1) {
            currentStates[index] = currentStates[index].copy(formattedContent = encoded)
            _manifestStates.value = currentStates
            println("DEBUG: Updated formatted content for $currentPath")
        } else {
            println("DEBUG: Failed to find state for $currentPath")
        }
    }
} 