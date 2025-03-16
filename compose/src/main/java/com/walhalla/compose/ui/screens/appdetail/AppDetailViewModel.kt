package com.walhalla.compose.ui.screens.appdetail

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.model.PackageMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _appInfo = MutableStateFlow<PackageMeta?>(null)
    val appInfo: StateFlow<PackageMeta?> = _appInfo.asStateFlow()

    fun loadAppInfo(packageName: String) {
        viewModelScope.launch {
            try {
                val packageManager = getApplication<Application>().packageManager
                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
                
                _appInfo.value = PackageMeta.Builder(packageInfo.packageName).apply {
//                    label(packageManager.getApplicationLabel(packageInfo.applicationInfo))
//                    versionName(packageInfo.versionName)
//                    versionCode(packageInfo.longVersionCode)
//                    applicationIcon(packageManager.getApplicationIcon(packageName))
//                    applicationInfo(packageInfo.applicationInfo)
//                    firstInstallTime(packageInfo.firstInstallTime)
//                    lastUpdateTime(packageInfo.lastUpdateTime)
                }.build()
            } catch (e: PackageManager.NameNotFoundException) {
                // Handle error
            }
        }
    }
} 