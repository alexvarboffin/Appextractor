package com.walhalla.appextractor.fragment

import com.walhalla.appextractor.BaseUtilsCallback


interface QCallback : BaseUtilsCallback {
    fun showProgress()

    fun hideProgress()

    //void successResult(Result<TwitterSession> result);
    fun handleException(exception: kotlin.Exception?)

    fun success(text: kotlin.Int)
}
