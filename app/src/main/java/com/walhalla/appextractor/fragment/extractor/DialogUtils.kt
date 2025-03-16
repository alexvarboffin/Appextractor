package com.walhalla.appextractor.fragment.extractor

import android.app.ProgressDialog
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.walhalla.appextractor.R


object DialogUtils {
    fun loadDialog(context: Context, icon: Int): ProgressDialog {
        val draw =
            ResourcesCompat.getDrawable(context.resources, R.drawable.custom_progressbar, null)
        val pd = ProgressDialog(context)
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pd.isIndeterminate = false //If we need onProgress bar update
        pd.setCanceledOnTouchOutside(false)
        pd.setCancelable(false)
        pd.setProgressDrawable(draw)
        pd.setIcon(icon)
        pd.setTitle(R.string.alert_dialog_title)
        pd.setMessage(context.getString(R.string.alert_dialog_text))
        pd.max = 100
        return pd
    }
}
