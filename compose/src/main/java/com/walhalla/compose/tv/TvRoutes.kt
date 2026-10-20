package com.walhalla.compose.tv

object TvRoutes {
    const val Shell = "shell"
    const val AppDetail = "app_detail/{packageName}"
    const val Manifest = "manifest"

    fun appDetail(packageName: String) = "app_detail/$packageName"
}
