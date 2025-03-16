package com.walhalla.compose.ui.screens.manifest

import android.content.pm.ActivityInfo
import android.content.pm.ProviderInfo
import android.content.pm.ServiceInfo

sealed class ManifestState {
    object Loading : ManifestState()
    data class Error(val message: String) : ManifestState()
    data class Success(
        val activities: List<ActivityInfo>,
        val services: List<ServiceInfo>,
        val receivers: List<ActivityInfo>,
        val providers: List<ProviderInfo>,
        val permissions: List<String>,
        val xmlContent: String? = null,
        val decompiledContent: String? = null
    ) : ManifestState()
}
