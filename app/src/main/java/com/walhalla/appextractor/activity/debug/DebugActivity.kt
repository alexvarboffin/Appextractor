package com.walhalla.appextractor.activity.debug

import android.os.Bundle

class DebugActivity : androidx.appcompat.app.AppCompatActivity(), QCallback {
    
    @Override
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(com.walhalla.appextractor.R.layout.activity_debug)

        if (bundle == null) {
            getSupportFragmentManager().beginTransaction()
                .add(com.walhalla.appextractor.R.id.container, DebugPagerFr.newInstance())
                .commit()
        }
    }

    @Override
    override fun showProgress() {
    }

    @Override
    override fun hideProgress() {
    }

    @Override
    fun handleException(exception: java.lang.Exception?) {
    }

    @Override
    override fun success(text: kotlin.Int) {
    }

    @Override
    fun copyToBuffer(value: kotlin.String?) {
    }

    @Override
    fun shareText(value: kotlin.String?) {
    }
}
