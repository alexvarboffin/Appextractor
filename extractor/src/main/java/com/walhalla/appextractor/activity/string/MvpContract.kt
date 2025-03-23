package com.walhalla.appextractor.activity.string

import androidx.annotation.StringRes
import com.walhalla.appextractor.activity.assets.StringItemViewModel

interface MvpContract {
    interface View {
        fun showResourceRawText(resource: StringItemViewModel, content: String)

        fun showError(message: String)

        fun showSuccess(list: List<StringItemViewModel>)

        fun successToast(@StringRes res: Int, aaa: String)

        fun errorToast(@StringRes res: Int, aaa: String)
    }
}
