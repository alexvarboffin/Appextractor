package com.walhalla.threader

import kotlinx.coroutines.CoroutineScope

abstract class AbstractInteractor(mThreadExecutor: CoroutineScope, mMainThread: CoroutineScope) {
    abstract fun cancel()
}