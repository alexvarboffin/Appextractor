package com.walhalla.appextractor.resources

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity

interface ManifestContract {

    interface View {
        fun showResourceRawText(resource: ResItem, content: String)

        fun showError(message: String)

        fun showSuccess(list: List<ResItem>)

        fun successToast(@StringRes res: Int, aaa: String)

        fun errorToast(@StringRes res: Int, aaa: String)
    }

    interface Presenter {
        fun loadManifestContent(packageName: String)
        fun saveAsset(context: Context, resource: ResItem)
        fun exportIconRequest(context: Context, resource: ResItem)

        fun readAssetRequest(context: Context, resource: ResItem)

        fun zipAllAssetsRequest(activity: FragmentActivity, resource: ResItem)
    }
}