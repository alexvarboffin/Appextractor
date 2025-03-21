package com.walhalla.compose.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.F0Presenter.Companion.DEVIDER_END
import com.walhalla.appextractor.sdk.F0Presenter.Companion.DEVIDER_START
import com.walhalla.appextractor.sdk.GetServicesUseCase
import com.walhalla.appextractor.sdk.HeaderCollapsedObject
import com.walhalla.appextractor.sdk.HeaderObject
import com.walhalla.appextractor.sdk.ServiceLine
import com.walhalla.appextractor.utils.PermissionUtils
import com.walhalla.compose.model.*
import com.walhalla.extractor.R
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

data class AppDetails(
    val permissions: Map<PermissionCategory, List<PermissionInfo>>,
    val activities: List<AppComponent>,
    val services: List<BaseViewModel>,
    val receivers: List<AppComponent>,
    val providers: List<AppComponent>
)

class AppDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _appDetails = MutableStateFlow<AppDetails?>(null)
    val appDetails: StateFlow<AppDetails?> = _appDetails.asStateFlow()
    var icons: MutableMap<Int, Drawable?> = HashMap()


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
                        val isGranted = pm.checkPermission(
                            permissionName,
                            app.packageName
                        ) == PackageManager.PERMISSION_GRANTED
                        val category = getPermissionCategory(permissionName)

                        PermissionInfo(
                            name = permissionName,
                            simpleName = permissionName.substringAfterLast('.'),
                            description = permissionInfo.loadDescription(pm)?.toString()
                                ?: getPermissionDescription(permissionName),
                            category = category,
                            protectionLevel = permissionInfo.protectionLevel,
                            isGranted = isGranted,
                            recommendation = getSecurityRecommendation(
                                category,
                                permissionInfo.protectionLevel
                            )
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                }?.groupBy { it.category } ?: emptyMap()

                val activities = packageInfo.activities?.map { activityInfo ->
                    AppComponent(
                        name = activityInfo.name,
                        exported = activityInfo.exported
                    )
                } ?: emptyList()


                val getServicesUseCase = GetServicesUseCase(getApplication<Application>().packageManager, getApplication<Application>())
                val services = getServicesUseCase.execute(packageInfo, icons)


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