package com.walhalla.appextractor.activity.resources

import android.graphics.drawable.Drawable
import com.walhalla.appextractor.activity.string.StringItem

class ResItem {
    @JvmField
    val drawable: Drawable?

    constructor(name: String, type: ResType) {
        this.fullPath = name
        this.type = type
        this.drawable = null
    }

    @JvmField
    val fullPath: String
    @JvmField
    val type: ResType

    constructor(name: String, drawable: Drawable?, type: ResType) {
        this.fullPath = name
        this.type = type
        this.drawable = drawable
    }

    companion object {
        @JvmStatic
        fun isImages(item: ResItem): Boolean {
            return item.fullPath.endsWith(".jpg") || item.fullPath.endsWith(".png")
        }

        fun isImages(fullPath: String): Boolean {
            return fullPath.endsWith(".jpg") || fullPath.endsWith(".png")
        }

        @JvmStatic
        fun isXml(resource: StringItem): Boolean {
            //return resource.value.startsWith("res/xml");
            return resource.value.endsWith(".xml")
        }
    }
}
