package com.walhalla.compose.model

data class SettingsModel(
    val googleDriveEnabled: Boolean = false,
    val dropboxEnabled: Boolean = false,
    val telegramEnabled: Boolean = false,
    val telegramToken: String = "",
    val telegramChatId: String = "",
    val appVersion: String = "",
    val appName: String = ""
) 