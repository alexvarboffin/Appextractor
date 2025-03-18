package com.walhalla.appextractor.common

class SimpleAppFeature(private val mText: String) : AppFeature {

    override fun toText(): CharSequence {
        return mText
    }
}