package com.walhalla.appextractor.activity.detail

import com.walhalla.appextractor.sdk.BaseViewModel


interface DetailsF0 {
    interface View {
        fun snack()

        fun swap(data: List<BaseViewModel>)
    }

    interface Presenter
}
