package com.walhalla.appextractor.activity.assets

import android.os.Parcelable
import com.walhalla.appextractor.model.ViewModel
import kotlinx.android.parcel.Parcelize

@Parcelize
sealed class ResType(override var id: Int) : Parcelable, ViewModel {
    object Dir : ResType(0)
    object Images : ResType(1)
    object File : ResType(2)
    object Xml : ResType(3)
    object StringRes : ResType(4)
    object Empty : ResType(5)
    object Error : ResType(6)
}