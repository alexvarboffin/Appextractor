package com.walhalla.appextractor.utils

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider

import com.walhalla.appextractor.utils.DLog.handleException
import com.walhalla.appextractor.utils.ShareUtils.KEY_FILE_PROVIDER

import java.io.File
import java.io.FileOutputStream

object BitmapUtils {
    public fun getLocalBitmapUri(activity: Activity, bitmap: Bitmap): Uri? {
        val file = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "icon.png")

        //DLog.d("[]" + file.getAbsolutePath() + " " + (bitmap == null));
        val APPLICATION_ID = activity.packageName
        var bmpUri: Uri? = null
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(activity, APPLICATION_ID + KEY_FILE_PROVIDER, file)
        } catch (e: Exception) {
            handleException(e)
        }
        return bmpUri
    }
}
