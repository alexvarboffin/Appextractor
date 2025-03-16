package com.walhalla.appextractor.resources

import android.graphics.drawable.Drawable

class ResItem {

    val drawable: Drawable?

    constructor(name: String, type: ResType) {
        this.fullPath = name
        this.type = type
        this.drawable = null
    }


    val fullPath: String

    val type: ResType

    constructor(name: String, drawable: Drawable?, type: ResType) {
        this.fullPath = name
        this.type = type
        this.drawable = drawable
    }

    companion object {

        fun isImages(item: ResItem): Boolean {
            return item.fullPath.endsWith(".jpg") || item.fullPath.endsWith(".png")
        }

        fun isImages(fullPath: String): Boolean {
            return fullPath.endsWith(".jpg") || fullPath.endsWith(".png")
        }


        fun isXml(resource: StringItemViewModel): Boolean {
            //return resource.value.startsWith("res/xml");
            return resource.text.endsWith(".xml")
        }
    }
}
