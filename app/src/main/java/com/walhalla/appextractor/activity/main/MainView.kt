package com.walhalla.appextractor.activity.main

import androidx.annotation.StringRes
import com.walhalla.appextractor.model.LogViewModel
import com.walhalla.appextractor.model.PackageMeta
import java.io.File

interface MainView {
    fun debugHideProgress(size: Int)

    fun printOutput(message: LogViewModel)

    //void makeProgressBar(int size);
    fun debugShowProgress(i: Int, v0: Int)

    fun makeSnackBar(file: File)

    fun failureExtracted(@StringRes id: Int)

    fun successToast(s: String)

    fun errorToast(s: String)

    fun saveIconRequest(packageInfo: PackageMeta)
}
