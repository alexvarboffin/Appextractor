package com.walhalla.appextractor.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.walhalla.ui.DLog.handleException

object IntentUtils {
    @JvmStatic
    fun openSettingsForPackageName(context: Context, packageName: String) {
        try {
            val intent = Intent()
                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setData(Uri.parse("package:$packageName"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context.startActivity(intent)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    fun openSettingsForPackageName2(context: Context, packageName: String?) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.setData(uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            handleException(e)
        }
    }
}
