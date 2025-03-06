package com.walhalla.appextractor.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.walhalla.appextractor.R
import com.walhalla.appextractor.fragment.QCallback
import com.walhalla.ui.BuildConfig
import com.walhalla.ui.DLog.d
import es.dmoral.toasty.Toasty
import java.util.Locale

abstract class BaseActivity : AppCompatActivity(), QCallback {
    override fun copyToBuffer(value: String) {
        if (BuildConfig.DEBUG) {
            val m = value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in m) {
                d("<BUFFER> $s")
            }
        }
        val tmp = String.format(getString(R.string.data_to_clipboard), value)
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard != null) {
            val clip = ClipData.newPlainText("NewValue", "" + value)
            clipboard.setPrimaryClip(clip)
            successMessage(this, tmp.uppercase(Locale.getDefault()))
        }
    }

    fun successMessage(context: Context?, message: String) {
        if (context != null) {
            Toasty.custom(
                context, message,
                ContextCompat.getDrawable(context, R.drawable.ic_details_settings),
                ContextCompat.getColor(context, R.color.colorPrimaryDark),
                ContextCompat.getColor(context, R.color.white), Toasty.LENGTH_SHORT,
                true, true
            )
                .show()
        }
    }
}
