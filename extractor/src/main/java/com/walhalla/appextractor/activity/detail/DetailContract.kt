package com.walhalla.appextractor.activity.detail

import android.graphics.drawable.Drawable

interface DetailContract {
    interface View {
        fun setTitleWithIcon(title: String?, packageName: String?, icon: Drawable?)
    }

    interface Presenter
}
