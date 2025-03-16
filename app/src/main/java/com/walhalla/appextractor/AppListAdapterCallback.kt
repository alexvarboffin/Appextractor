package com.walhalla.appextractor

import android.app.Activity
import android.content.Context
import android.view.View
import com.walhalla.appextractor.model.PackageMeta

interface AppListAdapterCallback : NotificationToast {
    fun nowExtractOneSelected(info: List<PackageMeta>, appName: Array<String>)

    fun openPackageOnGooglePlay(packageName: String)

    fun hideProgressBar()

    fun launchApp(context: Context, packageName: String)

    fun provideActivity(): Activity

    fun uninstallApp(packageName: String)

    fun count(size: Int)

    fun shareToOtherApp(generate_app_name: String)

    fun menuExtractSelected(v: View)

    fun saveIconRequest(packageInfo: PackageMeta)

    fun exportIconRequest(packageInfo: PackageMeta)
}