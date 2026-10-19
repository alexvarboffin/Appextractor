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

    var icons: MutableMap<Int, Drawable?> = HashMap()

    fun loadAppDetails(app: PackageMeta) {
        viewModelScope.launch(Dispatchers.Default) {
            _isLoading.value = true
            _appDetails.value = null
            try {
                val ctx = getApplication<Application>()
                val presenter = F0Presenter(
                    ctx,
                    app,
                    object : DetailsF0.View {
                        override fun snack() {}
                        override fun swap(data: List<BaseViewModel>) {}
                    }
                )
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
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${app.packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
