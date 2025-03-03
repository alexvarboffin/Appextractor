package com.walhalla.appextractor

import android.content.Context
import com.walhalla.ui.plugins.Launcher.openBrowser

object MarketUtils {
    // https://play.google.com/store/apps/category/COMMUNICATION
    @JvmStatic
    fun openGooglePlayCategory(context: Context, category: String) {
        openBrowser(
            context,
            "https://play.google.com/store/apps/category/$category"
        )
    }
}
