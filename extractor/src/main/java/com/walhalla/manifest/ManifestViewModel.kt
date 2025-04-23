package com.walhalla.manifest

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

import com.walhalla.appextractor.model.PackageMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ManifestUiState(
//    val isLoading: Boolean = false,
//    val userMessage: Int? = null,
//    val manifest: String? = null
    val title: String = "",
    val packageName: String = "",
    val icon: Drawable? = null
)

class ManifestViewModel : ViewModel() {

    private val _data = MutableStateFlow(ManifestUiState())
    val data: StateFlow<ManifestUiState> = _data.asStateFlow()

    fun doStuff(packageManager: PackageManager, meta: PackageMeta) {
        try {
            val applicationInfo = packageManager.getApplicationInfo(meta.packageName, 0)
            val title = applicationInfo.loadLabel(packageManager)
            val icon = applicationInfo.loadIcon(packageManager)
            _data.value = _data.value.copy(
                title = title.toString(),
                packageName = applicationInfo.packageName,
                icon = icon
            )

        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }
    }
}