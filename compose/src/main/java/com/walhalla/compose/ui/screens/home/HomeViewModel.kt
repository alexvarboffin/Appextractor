package com.walhalla.compose.ui.screens.home

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.model.PackageMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _apps = MutableStateFlow<List<PackageMeta>>(emptyList())
    val apps: StateFlow<List<PackageMeta>> = _apps.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val packageManager = getApplication<Application>().packageManager
            val installedApps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
                .map { packageInfo ->
                    PackageMeta.Builder(packageInfo.packageName).apply {
//                        label(packageManager.getApplicationLabel(packageInfo.applicationInfo))
//                        versionName(packageInfo.versionName)
//                        versionCode(packageInfo.longVersionCode)
//                        applicationIcon(packageManager.getApplicationIcon(packageInfo.packageName))
//                        applicationInfo(packageInfo.applicationInfo)
//                        firstInstallTime(packageInfo.firstInstallTime)
//                        lastUpdateTime(packageInfo.lastUpdateTime)
                    }.build()
                }
                .sortedBy { it.label.toString().lowercase() }
            
            _apps.value = installedApps
        }
    }

    fun searchApps(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadInstalledApps()
                return@launch
            }

            val filteredApps = _apps.value.filter { app ->
                app.label.toString().contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true)
            }
            _apps.value = filteredApps
        }
    }
} 