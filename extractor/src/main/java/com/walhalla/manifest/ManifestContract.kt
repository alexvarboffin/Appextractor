package com.walhalla.manifest

import android.graphics.drawable.Drawable

interface ManifestContract {
    interface View {
        fun setTitleWithIcon(title: String?, packageName: String?, icon: Drawable?)
    }

    interface Presenter
}
