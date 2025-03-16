package com.walhalla.appextractor.activity.resources.p002

import com.walhalla.appextractor.resources.ResItem

interface ManifestContract2 {
    interface View {
        fun showManifestContent(content: String)
        fun showError(message: String)

        fun showSuccess(list: List<ResItem>)
    }

    open interface Presenter {
        fun loadManifestContent(packageName: String?)
    }
}
