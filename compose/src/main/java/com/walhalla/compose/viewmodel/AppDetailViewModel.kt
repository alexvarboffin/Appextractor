package com.walhalla.compose.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.model.PackageMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AppComponent(
    val name: String,
    val exported: Boolean
)

data class Permission(
    val name: String,
    val description: String?
)

data class AppDetails(
    val permissions: List<Permission>,
    val activities: List<AppComponent>,
    val services: List<AppComponent>,
    val receivers: List<AppComponent>,
    val providers: List<AppComponent>
)

class AppDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _appDetails = MutableStateFlow<AppDetails?>(null)
    val appDetails: StateFlow<AppDetails?> = _appDetails.asStateFlow()

    fun loadAppDetails(app: PackageMeta) {
        viewModelScope.launch {
            try {
                val pm = getApplication<Application>().packageManager
                val packageInfo = pm.getPackageInfo(
                    app.packageName,
                    PackageManager.GET_PERMISSIONS or
                            PackageManager.GET_ACTIVITIES or
                            PackageManager.GET_SERVICES or
                            PackageManager.GET_RECEIVERS or
                            PackageManager.GET_PROVIDERS
                )

                val permissions = packageInfo.requestedPermissions?.mapNotNull { permissionName ->
                    try {
                        val permissionInfo = pm.getPermissionInfo(permissionName, 0)
                        Permission(
                            name = permissionName,
                            description = permissionInfo.loadDescription(pm)?.toString()
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        Permission(name = permissionName, description = null)
                    }
                } ?: emptyList()

                val activities = packageInfo.activities?.map { activityInfo ->
                    AppComponent(
                        name = activityInfo.name,
                        exported = activityInfo.exported
                    )
                } ?: emptyList()

                val services = packageInfo.services?.map { serviceInfo ->
                    AppComponent(
                        name = serviceInfo.name,
                        exported = serviceInfo.exported
                    )
                } ?: emptyList()

                val receivers = packageInfo.receivers?.map { receiverInfo ->
                    AppComponent(
                        name = receiverInfo.name,
                        exported = receiverInfo.exported
                    )
                } ?: emptyList()

                val providers = packageInfo.providers?.map { providerInfo ->
                    AppComponent(
                        name = providerInfo.name,
                        exported = providerInfo.exported
                    )
                } ?: emptyList()

                _appDetails.value = AppDetails(
                    permissions = permissions,
                    activities = activities,
                    services = services,
                    receivers = receivers,
                    providers = providers
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openInSettings(app: PackageMeta) {
        val context = getApplication<Application>()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${app.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            .format(Date(timestamp))
    }
} 