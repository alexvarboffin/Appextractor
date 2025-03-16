package com.walhalla.appextractor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.walhalla.ui.DLog.d

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

object Troubleshooting {

    @JvmStatic
    @Throws(FileNotFoundException::class)
    fun streamLoader(context: Context, file: File): InputStream? {
        d("###" + " --> " + file.absolutePath)

        val inputStream: InputStream?
        if (Build.VERSION.SDK_INT >= 29)  //Android 10 level 29 (Android 10).
        {
            //Uri fileUri = Uri.parse(file.getAbsolutePath());
            val fileUri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
//            final ParcelFileDescriptor pfd;
//
//            pfd = context.getContentResolver().openFileDescriptor(fileUri, "r", null);
//            inputStream = new FileInputStream(pfd.getFileDescriptor());
            inputStream = context.contentResolver.openInputStream(fileUri)
        } else {
            inputStream = FileInputStream(file)
        }
        return inputStream
    }

    @JvmStatic
    @SuppressLint("ObsoleteSdkInt")
    fun defLocation(): File {
        val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            Environment.getExternalStorageDirectory()
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            //file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        }
        println("Out: " + file.absolutePath + " " + file.exists())
        return file
    }
}
