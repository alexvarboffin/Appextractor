package com.walhalla.appextractor.compat

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

object ComV19 {
    fun getDrawable(context: Context, res: Int): Drawable? {
        val draw = if (Build.VERSION.SDK_INT > 19) {
            ContextCompat.getDrawable(context, res)
        } else {
            //Вектор падает на 4.4 sdk 19
            AppCompatResources.getDrawable(context, res)
        }
        return draw
    }
}
