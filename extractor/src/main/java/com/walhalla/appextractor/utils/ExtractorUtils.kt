package com.walhalla.appextractor.utils

import android.content.Context
import android.os.Build
import java.io.File

object ExtractorUtils {
    @Throws(Exception::class)
    fun getAllApkFilesForCurrentPackage(context: Context, pkg: String): List<File> {
        val applicationInfo = context.packageManager.getApplicationInfo(pkg, 0)

        val apkFiles: MutableSet<File> = LinkedHashSet()
        apkFiles.add(File(applicationInfo.publicSourceDir))             // applicationInfo.publicSourceDir always equal applicationInfo.sourceDir
        apkFiles.add(File(applicationInfo.sourceDir))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (applicationInfo.splitPublicSourceDirs != null) {
                for (splitPath in applicationInfo.splitPublicSourceDirs!!) apkFiles.add(
                    File(
                        splitPath
                    )
                )
            }
        }
        return apkFiles.toList()
    }


}
