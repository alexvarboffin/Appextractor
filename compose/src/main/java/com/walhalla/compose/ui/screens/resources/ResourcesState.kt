package com.walhalla.compose.ui.screens.resources

sealed class ResourcesState {
    object Loading : ResourcesState()
    data class Error(val message: String) : ResourcesState()
    data class Success(val resources: AppResources) : ResourcesState()
}

data class AppResources(
    val strings: Map<String, String> = emptyMap(),
    val drawables: Map<String, Any> = emptyMap(),
    val layouts: List<String> = emptyList()
) 