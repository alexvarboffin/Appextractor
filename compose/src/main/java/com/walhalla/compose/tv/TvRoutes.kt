package com.walhalla.compose.tv

object TvRoutes {
    const val Shell = "shell"
    const val AppDetail = "app_detail/{packageName}"

    fun appDetail(packageName: String) = "app_detail/$packageName"
}
