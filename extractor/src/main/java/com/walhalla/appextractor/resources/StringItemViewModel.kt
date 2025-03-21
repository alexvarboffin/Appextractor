package com.walhalla.appextractor.resources

import com.walhalla.appextractor.model.ViewModel

class StringItemViewModel(val name: String, val text: String, val icon: Int?, val type: ResType = ResType.StringRes) :
    ViewModel {
    constructor(name: String, value: String, type: ResType) : this(
        name,
        value,
        null,
        type
    )

    override val id = type.id

    companion object {

        fun isImages(item: StringItemViewModel): Boolean {
            return item.name.endsWith(".jpg") || item.name.endsWith(".png")
        }

        fun isImages(fullPath: String): Boolean {
            return fullPath.endsWith(".jpg") || fullPath.endsWith(".png")
        }
    }
}
