package com.walhalla.appextractor.fragment

import android.content.Context
import androidx.fragment.app.Fragment

abstract class BaseFragment  //protected View mRootView;
    : Fragment() {
    abstract fun fab()

    @JvmField
    protected var callback: QCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is QCallback) {
            callback = context
        } else {
            throw RuntimeException("$context must implement QCallback")
        }
    }
}
