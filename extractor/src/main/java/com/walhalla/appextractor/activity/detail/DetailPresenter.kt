package com.walhalla.appextractor.activity.detail

import android.content.Context
import android.content.pm.PackageManager
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.DLog.handleException


class DetailPresenter(context: Context, meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta, view: DetailContract.View) :
    DetailContract.Presenter {
    private val mView: DetailContract.View
    private val context: Context

    init {
        val manager = context.packageManager
        this.context = context
        this.mView = view
        doStuff(manager, meta)
    }

    private fun doStuff(packageManager: PackageManager, meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta) {
        try {
            val applicationInfo = packageManager.getApplicationInfo(
                meta.packageName!!, 0
            )
            val title = applicationInfo.loadLabel(packageManager)
            val icon = applicationInfo.loadIcon(packageManager)
            mView.setTitleWithIcon(title.toString(), applicationInfo.packageName, icon)
        } catch (e: PackageManager.NameNotFoundException) {
            handleException(e)
        }
    }
}
