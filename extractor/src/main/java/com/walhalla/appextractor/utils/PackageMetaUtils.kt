package com.walhalla.appextractor.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build

object PackageMetaUtils {

    fun drw(context: Context, packageName: String): Bitmap? {
        var bitmap: Bitmap? = null
        val pm = context.packageManager

        //am = context.createPackageContext(packageName, 0).getAssets();
        //Drawable drawable = packageInfo.applicationInfo.loadIcon(pm);
        try {
            val applicationInfo = pm.getApplicationInfo(packageName, 0)
            val drawable = applicationInfo.loadIcon(pm)
            //AdaptiveIconDrawable
            if (drawable is BitmapDrawable) {
                bitmap = drawable.bitmap
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //26
                    bitmap = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return bitmap
        }
        return bitmap
    }
}
