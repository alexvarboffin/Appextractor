package com.walhalla.appextractor.model.backup

import com.walhalla.appextractor.model.common.AppFeature

class SimpleAppFeature(private val mText: String) : AppFeature {

    override fun toText(): CharSequence {
        return mText
    }
}