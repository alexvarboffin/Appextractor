package com.walhalla.compose.launcher

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle

import com.walhalla.compose.phone.MainActivity as PhoneMainActivity
import com.walhalla.compose.tv.MainActivity as TvMainActivity

/**
 * Sole device-routing entry (LAUNCHER). Does not host Compose UI.
 * Detection matches FireTv: [Configuration.UI_MODE_TYPE_TELEVISION].
 */
class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isTv =
            (resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK) ==
                Configuration.UI_MODE_TYPE_TELEVISION
                    //|| (BuildConfig.DEBUG && FORCE_TV_IN_DEBUG)
        val target = if (isTv) TvMainActivity::class.java else PhoneMainActivity::class.java
        startActivity(Intent(this, target))
        finish()
    }

    private companion object {
        /** Set `true` to open the TV shell on a phone during debug installs. */
        const val FORCE_TV_IN_DEBUG = true
    }
}
