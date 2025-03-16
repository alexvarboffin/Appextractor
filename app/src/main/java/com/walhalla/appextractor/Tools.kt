package com.walhalla.appextractor

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat
import com.walhalla.appextractor.compat.ComV19
import com.walhalla.ui.BuildConfig
import com.walhalla.ui.DLog.d
import es.dmoral.toasty.Toasty

object Tools {
    fun copyToClipboard(value: String, activity: Activity) {
        if (BuildConfig.DEBUG) {
            val m = value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in m) {
                d("<BUFFER> $s")
            }
        }
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard != null) {
            val clip = ClipData.newPlainText("packageName", "" + value)
            clipboard.setPrimaryClip(clip)
        }
        val tmp = String.format(activity.getString(R.string.data_to_clipboard), value)
        Toasty.custom(
            activity, tmp,
            ComV19.getDrawable(activity, R.drawable.ic_info),
            ContextCompat.getColor(activity, R.color.colorPrimaryDark),
            ContextCompat.getColor(activity, R.color.white), Toasty.LENGTH_SHORT, true, true
        ).show()
    }

    //    public static String convertStreamToReadableString(String text)
    //    {
    //        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
    //                "<html><head>" +
    //                "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
    //                "<head><body>" +
    //                text +
    //                "</body></html>";
    //    }
    fun convertStreamToReadableString(text: String): String {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>$text"
    }
}
