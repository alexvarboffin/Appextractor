package com.walhalla.appextractor.activity.debug

import android.os.Bundle
import com.walhalla.appextractor.fragment.QCallback

class DebugActivity : androidx.appcompat.app.AppCompatActivity(), QCallback {

    
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(com.walhalla.appextractor.R.layout.activity_debug)

        if (bundle == null) {
            getSupportFragmentManager().beginTransaction()
                .add(com.walhalla.appextractor.R.id.container, DebugPagerFr.newInstance())
                .commit()
        }
    }

    
    override fun showProgress() {
    }

    
    override fun hideProgress() {
    }

    
    override fun handleException(exception: kotlin.Exception?) {
    }

    
    override fun success(text: kotlin.Int) {
    }

    
    override fun copyToBuffer(value: kotlin.String) {
    }

    
    override fun shareText(value: kotlin.String) {
    }
}
