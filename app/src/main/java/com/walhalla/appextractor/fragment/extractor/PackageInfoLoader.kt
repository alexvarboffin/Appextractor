package com.walhalla.appextractor.fragment.extractor

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import com.walhalla.appextractor.ApkUtils
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.XmlUtils
import java.io.File
import java.lang.ref.WeakReference

class PackageInfoLoader(a: Context, private val callback: Callback) : AsyncTask<Void, PackageMeta, Void>() {
    ////progressDialog.getWindow().setGravity(Gravity.BOTTOM);
    private val weakReference =
        WeakReference(a)

    interface Callback {
        fun onProgressUpdate(value: PackageMeta)

        fun onPostExecute()
    }

    override fun doInBackground(vararg params: Void): Void? {
        val a = weakReference.get()
        if (a != null) {
            val pm = a.packageManager
            @SuppressLint("QueryPermissionsNeeded") val packageInfos = pm.getInstalledPackages(
                PackageManager.GET_META_DATA
            )
            for (packageInfo in packageInfos) {
                //publishProgress(packageInfo);


                val applicationInfo = packageInfo.applicationInfo
                var hasSplits = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    hasSplits =
                        applicationInfo!!.splitPublicSourceDirs != null && applicationInfo.splitPublicSourceDirs!!.size > 0
                }

                val pining = XmlUtils.isPinningEnabled(a, applicationInfo!!.packageName)

                val meta = PackageMeta.Builder(applicationInfo.packageName)
                    .setLabel(applicationInfo.loadLabel(pm).toString())
                    .setHasSplits(hasSplits).setHasPining(pining)
                    .setIsSystemApp((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)
                    .setVersionCode(if (ApkUtils.apiIsAtLeast(Build.VERSION_CODES.P)) packageInfo.longVersionCode else packageInfo.versionCode.toLong())
                    .setVersionName(packageInfo.versionName)
                    .setIcon(applicationInfo.icon)
                    .setInstallTime(packageInfo.firstInstallTime)
                    .setUpdateTime(packageInfo.lastUpdateTime)
                    .build()

                val file = File(applicationInfo.publicSourceDir)
                val longsize = file.length()
                var size = if (longsize > 1024 && longsize <= 1024 * 1024) {
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

                publishProgress(meta)
            }
        }
        return null
    }

    override fun onProgressUpdate(vararg values: PackageMeta) {
        super.onProgressUpdate(*values)
        callback?.onProgressUpdate(values[0])
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        callback?.onPostExecute()
    }
}
