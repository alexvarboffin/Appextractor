package com.walhalla.appextractor.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

class ApkUtils {
    private fun readFile(file: File): ByteArray? {
        // Open fileBuffer
        var file1: RandomAccessFile? = null
        try {
            file1 = RandomAccessFile(file, "r")
            // Get and check length
            val longlength = file1.length()
            val length = longlength.toInt()
            if (length.toLong() != longlength) throw IOException("File size >= 2 GB")
            // Read fileBuffer and return data
            val data = ByteArray(length)
            file1.readFully(data)
            return data
        } catch (e: IOException) {
            handleException(e)
        } finally {
            try {
                file1?.close()
            } catch (e: IOException) {
                handleException(e)
            }
        }
        return null
    }

    companion object {
        const val KEY_UNINSTALL: Int = 1443

        fun uninstallApp0(activity: Activity, packageName: String) {
            try {
                val intent = Intent(Intent.ACTION_DELETE)
                intent.setData(Uri.parse("package:$packageName"))
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

//            startActivity(intent);

//            Uri packageURI = Uri.parse("package:" + packageName);
//            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//            activity.startActivityForResult(uninstallIntent, KEY_UNINSTALL);

                //var1
                d("Uninstall - > $packageName")

                //            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
//            intent.setData(Uri.parse("package:" + packageName));
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                //var2
                activity.startActivityForResult(intent, KEY_UNINSTALL)
            } catch (e: ActivityNotFoundException) {
                handleException(e)
                //Toasty.error(activity, "" + e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

        fun apiIsAtLeast(sdkInt: Int): Boolean {
            return Build.VERSION.SDK_INT >= sdkInt
        }
    }
}
