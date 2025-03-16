package com.walhalla.appextractor.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import com.walhalla.ui.DLog.d

object ContextUtils {
    fun test0(context: Context) {
        val resourceId = 0x7f080158
        val packageName = "com.walhalla.mtprotolist"
        val otherContext: Context
        try {
            otherContext = context.createPackageContext(packageName, 0)
            val resourceName = otherContext.resources.getResourceEntryName(resourceId)
            val m = isDrawableResourceExist(otherContext, resourceName)
            d("Resource name: $resourceName $m")
        } catch (e: Resources.NotFoundException) {
            d("Resource not found for ID: $resourceId")
        } catch (e: PackageManager.NameNotFoundException) {
            d("@@ PACKAGE NOT FOUND @@@ $packageName $resourceId")
        }
    }

    fun isDrawableResourceExist(context: Context, resourceName: String?): Boolean {
        val resourceId =
            context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        return resourceId != 0
    }
}
