package com.walhalla.compose.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.walhalla.compose.R

/** Store links per flavor: src/google|rustore/res/values/market.xml */
object MarketLinks {

    fun appUrl(ctx: Context, pkg: String = ctx.packageName): String =
        ctx.getString(R.string.market_app_url, pkg)

    fun appWebUrl(ctx: Context, pkg: String = ctx.packageName): String =
        ctx.getString(R.string.market_app_web_url, pkg)

    fun openApp(ctx: Context, pkg: String = ctx.packageName) =
        open(ctx, appUrl(ctx, pkg), appWebUrl(ctx, pkg))

    fun openDeveloper(ctx: Context) =
        open(ctx, ctx.getString(R.string.market_dev_url), ctx.getString(R.string.market_dev_web_url))

    private fun open(ctx: Context, url: String, fallback: String) {
        try {
            start(ctx, url)
        } catch (e: ActivityNotFoundException) {
            try {
                start(ctx, fallback)
            } catch (e2: ActivityNotFoundException) {
                com.walhalla.ui.DLog.handleException(e2)
            }
        }
    }

    private fun start(ctx: Context, url: String) {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}