package com.walhalla.appextractor.activity.debug

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment
import com.walhalla.appextractor.utils.ApkUtils
import com.walhalla.appextractor.model.PackageMeta
import java.io.File

open class DemoFragment : Fragment() {

    protected var a: List<ApplicationInfo> = emptyList()

    protected var b: List<PackageMeta> = emptyList()
    protected var infos: List<PackageInfo> = emptyList()


    @SuppressLint("QueryPermissionsNeeded")
    protected fun fullMeta(pm: PackageManager) {
        val aaa: MutableList<ApplicationInfo> = ArrayList()
        val bbb: MutableList<PackageMeta> = ArrayList()

        //infos = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        infos = pm.getInstalledPackages(
            (PackageManager.GET_PERMISSIONS
                    or PackageManager.GET_SIGNATURES
                    or PackageManager.GET_META_DATA
                    or PackageManager.GET_ACTIVITIES
                    or PackageManager.GET_SERVICES
                    or PackageManager.GET_PROVIDERS
                    or PackageManager.GET_RECEIVERS)
        )

        for (packageInfo in infos!!) {
            val applicationInfo = packageInfo.applicationInfo
            applicationInfo?.let { aaa.add(it) }


            var hasSplits = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                hasSplits =
                    applicationInfo!!.splitPublicSourceDirs != null && applicationInfo.splitPublicSourceDirs!!.size > 0
            }
            val meta = PackageMeta.Builder(
                applicationInfo!!.packageName
            ).label(applicationInfo.loadLabel(pm).toString()).setHasSplits(hasSplits)
                .setIsSystemApp((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
                .setVersionCode(
                    if (ApkUtils.apiIsAtLeast(
                            Build.VERSION_CODES.P
                        )
                    ) packageInfo.longVersionCode else packageInfo.versionCode.toLong()
                ).setVersionName(packageInfo.versionName).setIcon(
                    applicationInfo.icon
                ).setInstallTime(packageInfo.firstInstallTime)
                .setUpdateTime(packageInfo.lastUpdateTime)
                .build()

            val file = File(applicationInfo.publicSourceDir)
            val longsize = file.length()
            val size = if (longsize > 1024 && longsize <= 1024 * 1024) {
                ((longsize / 1024).toString() + " KB")
            } else if (longsize > 1024 * 1024 && longsize <= 1024 * 1024 * 1024) {
                ((longsize / (1024 * 1024)).toString() + " MB")
            } else {
                ((longsize / (1024 * 1024 * 1024)).toString() + " GB")
            }
            meta.size = size
            meta.sourceDir = applicationInfo.sourceDir
            meta.packageName = packageInfo.packageName
            meta.firstInstallTime = packageInfo.firstInstallTime
            meta.lastUpdateTime = packageInfo.lastUpdateTime
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                meta.category = applicationInfo.category
            }
            bbb.add(meta)
        }
        a = aaa
        b = bbb
    }
}
