package com.walhalla.appextractor.activity.assets

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity

interface MvpContract {

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

        fun zipAllAssetsRequest(activity: Context, resource: ResItem)
    }
}