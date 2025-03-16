package com.walhalla.compose.ui.screens.resources

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResourcesViewModel(application: Application) : AndroidViewModel(application) {
    private val _resourcesState = MutableStateFlow<ResourcesState>(ResourcesState.Loading)
    val resourcesState: StateFlow<ResourcesState> = _resourcesState.asStateFlow()

    fun loadResources(packageName: String) {
        viewModelScope.launch {
            try {
                _resourcesState.value = ResourcesState.Loading
                
                val packageManager = getApplication<Application>().packageManager
                val context = getApplication<Application>().createPackageContext(
                    packageName,
                    Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
                )
                val resources = context.resources

                val strings = loadStrings(resources, packageName)
                val drawables = loadDrawables(resources, packageName)
                val layouts = loadLayouts(resources, packageName)

                _resourcesState.value = ResourcesState.Success(
                    AppResources(
                        strings = strings,
                        drawables = drawables,
                        layouts = layouts
                    )
                )
            } catch (e: Exception) {
                _resourcesState.value = ResourcesState.Error(e.localizedMessage ?: "Failed to load resources")
            }
        }
    }

    private fun loadStrings(resources: Resources, packageName: String): Map<String, String> {
        val strings = mutableMapOf<String, String>()
        try {
            val fields = Class.forName("$packageName.R\$string").fields
            for (field in fields) {
                try {
                    val resourceId = field.getInt(null)
                    val value = resources.getString(resourceId)
                    strings[field.name] = value
                } catch (e: Exception) {
                    // Skip this string
                }
            }
        } catch (e: Exception) {
            // Package has no string resources
        }
        return strings
    }

    private fun loadDrawables(resources: Resources, packageName: String): Map<String, Any> {
        val drawables = mutableMapOf<String, Any>()
        try {
            val fields = Class.forName("$packageName.R\$drawable").fields
            for (field in fields) {
                try {
                    val resourceId = field.getInt(null)
                    val drawable = resources.getDrawable(resourceId, null)
                    drawables[field.name] = drawable
                } catch (e: Exception) {
                    // Skip this drawable
                }
            }
        } catch (e: Exception) {
            // Package has no drawable resources
        }
        return drawables
    }

    private fun loadLayouts(resources: Resources, packageName: String): List<String> {
        val layouts = mutableListOf<String>()
        try {
            val fields = Class.forName("$packageName.R\$layout").fields
            for (field in fields) {
                layouts.add(field.name)
            }
        } catch (e: Exception) {
            // Package has no layout resources
        }
        return layouts
    }
} 