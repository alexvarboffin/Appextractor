package com.walhalla.appextractor.sdk

import com.walhalla.appextractor.resources.ResType

object ResourcesToolForPlugin {
    const val ANIM: String = "anim"
    const val ANIMATOR: String = "animator"
    const val ARRAY: String = "array"
    const val ATTR: String = "attr"
    const val BOOL: String = "bool"
    const val COLOR: String = "color"
    const val DIMEN: String = "dimen"
    const val DRAWABLE: String = "drawable"
    const val ID: String = "id"
    const val INTEGER: String = "integer"
    const val INTERPOLATOR: String = "interpolator"
    const val LAYOUT: String = "layout"
    const val MENU: String = "menu"
    const val RAW: String = "raw"
    const val STRING: String = "string"
    const val STYLE: String = "style"
    const val STYLEABLE: String = "styleable"
    const val TRANSITION: String = "transition"
    const val XML: String = "xml"

    @JvmStatic
    fun getIconBySourceType(resourceType: String): ResType? {
        if (XML == resourceType) {
            return ResType.Xml
        }
        if (STRING == resourceType) {
            return ResType.StringRes
        }
        return null
    }
}
