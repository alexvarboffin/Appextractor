package com.walhalla.appextractor.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
sealed class LogType(override var id: Int) : Parcelable, ViewModel {

    object Error : LogType(12)

    object Success : LogType(13)

    object File : LogType(14)

    object Empty : LogType(15)
}

@Parcelize
open class LogViewModel(val type: LogType, open var icon: Int, open val text: String) : ViewModel, Parcelable {

    override val id = type.id
}



@Parcelize
data class LFileViewModel(val file: File, override var icon: Int, override val text: String) : LogViewModel(
    type = LogType.File,
    icon = icon,
    text = text
) {
    override val id = type.id
}

//class LErrorViewModel(icon: Int, msg: String) : LogViewModel(icon, msg) {
//    override fun getID(): Long {
//        return TYPE_ERROR.toLong()
//    }
//
//    companion object {
//        const val TYPE_ERROR: Int = 5
//    }
//}
//class LSuccessViewModel(icon: Int, msg: String) : LogViewModel(icon, msg) {
//    override fun getID(): Long {
//        return -1
//    }
//}


//: ViewModel
