package com.walhalla.appextractor.presenter

interface ManifestPresenter {
    fun configForPackage(packageName: String, apkPath0: String): Boolean

    companion object {
        const val TAG_MANIFEST: String = "manifest"
    }
}
