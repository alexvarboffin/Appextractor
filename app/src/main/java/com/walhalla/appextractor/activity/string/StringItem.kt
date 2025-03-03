package com.walhalla.appextractor.activity.string

import com.walhalla.appextractor.activity.resources.ResType
import com.walhalla.appextractor.model.ViewModel

class StringItem(
    @JvmField val name: String, @JvmField val value: String, @JvmField val drawable: Int?, @JvmField val type: ResType?) :
    ViewModel {
    constructor(name: String, value: String, type: ResType?) : this(name, value, null, type)

    override fun getID(): Long {
        return TYPE_ITEM_STRING.toLong()
    }

    companion object {
        const val TYPE_ITEM_STRING: Int = 6


        fun isImages(item: StringItem): Boolean {
            return item.name.endsWith(".jpg") || item.name.endsWith(".png")
        }

        fun isImages(fullPath: String): Boolean {
            return fullPath.endsWith(".jpg") || fullPath.endsWith(".png")
        }
    }
}
