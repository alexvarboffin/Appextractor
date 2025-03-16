package com.walhalla.compose.ui.screens.manifest

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.model.ViewModel
import com.walhalla.appextractor.presenter.BaseManifestPresenter
import com.walhalla.appextractor.presenter.ManifestPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManifestViewModel(application: Application) : AndroidViewModel(application), ViewModel, BaseManifestPresenter.ManifestCallback {
    
    override val id: Int = 0
    
    private val _manifestState = MutableStateFlow<ManifestState>(ManifestState.Loading)
    val manifestState: StateFlow<ManifestState> = _manifestState.asStateFlow()

    private val manifestPresenter: ManifestPresenter = object : BaseManifestPresenter(getApplication(), this@ManifestViewModel) {
        override fun configForPackage(packageName: String, apkPath0: String): Boolean {
            return try {
                val packageManager = context.packageManager
                val packageInfo = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_SERVICES or
                    PackageManager.GET_RECEIVERS or
                    PackageManager.GET_PROVIDERS or
                    PackageManager.GET_PERMISSIONS
                )

                _manifestState.value = ManifestState.Success(
                    activities = packageInfo.activities?.toList() ?: emptyList(),
                    services = packageInfo.services?.toList() ?: emptyList(),
                    receivers = packageInfo.receivers?.toList() ?: emptyList(),
                    providers = packageInfo.providers?.toList() ?: emptyList(),
                    permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
                )
                true
            } catch (e: Exception) {
                _manifestState.value = ManifestState.Error(e.localizedMessage ?: "Failed to load manifest")
                false
            }
        }
    }

    fun loadManifest(packageName: String) {
        viewModelScope.launch {
            _manifestState.value = ManifestState.Loading
            manifestPresenter.configForPackage(packageName, "")
        }
    }

    override fun showError(readingXml: String, throwable: Throwable?) {
        _manifestState.value = ManifestState.Error(throwable?.localizedMessage ?: readingXml)
    }

    override fun showManifestContent(content: String) {
        val currentState = _manifestState.value
        if (currentState is ManifestState.Success) {
            _manifestState.value = currentState.copy(xmlContent = content)
        }
    }

    override fun loadDataWithPatternHTML(encoded: String) {
        val currentState = _manifestState.value
        if (currentState is ManifestState.Success) {
            _manifestState.value = currentState.copy(decompiledContent = encoded)
        }
    }
} 