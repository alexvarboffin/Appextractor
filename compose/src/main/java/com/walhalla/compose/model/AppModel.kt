package com.walhalla.compose.model

import android.graphics.drawable.Drawable

data class AppModel(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val icon: Drawable?,
    val isSystemApp: Boolean,
    val installTime: Long,
    val updateTime: Long,
    val apkPath: String
) 