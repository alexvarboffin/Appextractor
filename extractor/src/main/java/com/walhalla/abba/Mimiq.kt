package com.walhalla.appextractor.abba

import android.content.Intent
import android.net.Proxy

object Mimiq {
    @JvmField
    var actions: Array<String> = arrayOf(
        Intent.ACTION_MAIN,  //category_launcher
        Intent.ACTION_VIEW,
        Intent.ACTION_INSTALL_PACKAGE,
        Intent.ACTION_UNINSTALL_PACKAGE,

        Intent.ACTION_GET_CONTENT,  //We not use
        Intent.ACTION_DELETE,
        Intent.ACTION_OPEN_DOCUMENT,
        Intent.ACTION_SEND,
        Intent.ACTION_SEND_MULTIPLE,
        Intent.ACTION_SENDTO,

        Proxy.PROXY_CHANGE_ACTION
        //            WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER,
        //            WallpaperService.SERVICE_INTERFACE

    )
}
