package com.walhalla.appextractor.activity

import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue

object Utilities {
    val ATLEAST_JB_MR2: Boolean = (Build.VERSION.SDK_INT >= 18)
    @JvmField
    val ATLEAST_KITKAT: Boolean = (Build.VERSION.SDK_INT >= 19)
    @JvmField
    val ATLEAST_LOLLIPOP: Boolean = (Build.VERSION.SDK_INT >= 21)
    val ATLEAST_LOLLIPOP_MR1: Boolean = (Build.VERSION.SDK_INT >= 22)
    @JvmField
    val ATLEAST_MARSHMALLOW: Boolean = (Build.VERSION.SDK_INT >= 23)
    @JvmField
    val ATLEAST_NOUGAT: Boolean = (Build.VERSION.SDK_INT >= 24)
    @JvmField
    val ATLEAST_NOUGAT_MR1: Boolean = (Build.VERSION.SDK_INT >= 25)
    val ATLEAST_OREO: Boolean = (Build.VERSION.SDK_INT >= 26)
    val ATLEAST_OREO_MR1: Boolean = (Build.VERSION.SDK_INT >= 27)
    const val ATLEAST_JB_MR1: Boolean = false


    fun pxFromDp(f2: Float, displayMetrics: DisplayMetrics?): Int {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f2, displayMetrics))
    }

    fun pxFromSp(f2: Float, displayMetrics: DisplayMetrics?): Int {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, f2, displayMetrics))
    }
}
