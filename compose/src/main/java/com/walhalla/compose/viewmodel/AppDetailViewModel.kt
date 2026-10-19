package com.walhalla.compose.viewmodel

import android.app.Application
import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.activity.detail.DetailsF0
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.F0Presenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppDetails(
    val infoLines: List<BaseViewModel>
)

class AppDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _appDetails = MutableStateFlow<AppDetails?>(null)
    val appDetails: StateFlow<AppDetails?> = _appDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _needsUsageAccess = MutableStateFlow(false)
    val needsUsageAccess: StateFlow<Boolean> = _needsUsageAccess.asStateFlow()

    var icons: MutableMap<Int, Drawable?> = HashMap()
    private var f0Presenter: F0Presenter? = null

    fun clearUsageAccessRequest() {
        _needsUsageAccess.value = false
    }

    fun loadAppDetails(app: PackageMeta) {
        viewModelScope.launch(Dispatchers.Default) {
            _isLoading.value = true
            _appDetails.value = null
            _needsUsageAccess.value = false
            try {
                val ctx = getApplication<Application>()
                val presenter = F0Presenter(
                    ctx,
                    app,
                    object : DetailsF0.View {
                        override fun snack() {
                            _needsUsageAccess.value = true
                        }
                        override fun swap(data: List<BaseViewModel>) {}
                    }
                )
                f0Presenter = presenter
                icons = presenter.icons
                _appDetails.value = AppDetails(infoLines = presenter.buildInfoList(ctx))
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun openInSettings(app: PackageMeta) {
        f0Presenter?.openSettingsRequest(getApplication())
            ?: run {
                val context = getApplication<Application>()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:${app.packageName}".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
    }

    fun launchExportedService(className: String) {
        f0Presenter?.onLaunchExportedService(className)
    }

    fun openUsageAccessSettings() {
        val context = getApplication<Application>()
        context.startActivity(
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY)
            }
        )
        clearUsageAccessRequest()
    }
}
