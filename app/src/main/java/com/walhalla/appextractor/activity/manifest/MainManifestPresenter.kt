package com.walhalla.appextractor.activity.manifest

import android.content.Context
import android.content.pm.PackageManager
import com.walhalla.appextractor.model.PackageMeta

class MainManifestPresenter(context: Context, meta: PackageMeta, view: ManifestContract.View) :
    ManifestContract.Presenter {
    private val mView: ManifestContract.View
    private val context: Context

    init {
        val manager = context.packageManager
        this.context = context
        this.mView = view
        doStuff(manager, meta)
    }

    private fun doStuff(packageManager: PackageManager, meta: PackageMeta) {
        try {
            val applicationInfo = packageManager.getApplicationInfo(meta.packageName, 0)
            val title = applicationInfo.loadLabel(packageManager)
            val icon = applicationInfo.loadIcon(packageManager)
            mView.setTitleWithIcon(title.toString(), applicationInfo.packageName, icon)
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }
    }
}
