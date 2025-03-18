package com.walhalla.compose.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.storage.LocalStorage
import com.walhalla.compose.model.SettingsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val localStorage = LocalStorage.getInstance(application)

    private val _settings = MutableStateFlow(SettingsModel())
    val settings: StateFlow<SettingsModel> = _settings.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val googleDriveEnabled =
                localStorage.settingsValue("key_google_drive_storage") as? Boolean ?: false
            val dropboxEnabled =
                localStorage.settingsValue("key_drop_box_storage") as? Boolean ?: false
            val telegramEnabled =
                localStorage.settingsValue("key_telegram_storage") as? Boolean ?: false
            val telegramToken = localStorage.settingsValue("key_telegram_token") as? String ?: ""
            val telegramChatId = localStorage.settingsValue("key_telegram_chat_id") as? String ?: ""

            _settings.value = SettingsModel(
                googleDriveEnabled = googleDriveEnabled,
                dropboxEnabled = dropboxEnabled,
                telegramEnabled = telegramEnabled,
                telegramToken = telegramToken,
                telegramChatId = telegramChatId,
                appVersion = getAppVersion(),
                appName = getAppName()
            )
        }
    }

    fun updateGoogleDriveEnabled(enabled: Boolean) {
        viewModelScope.launch {
            localStorage.saveSettings("key_google_drive_storage", enabled)
            _settings.value = _settings.value.copy(googleDriveEnabled = enabled)
        }
    }

    fun updateDropboxEnabled(enabled: Boolean) {
        viewModelScope.launch {
            localStorage.saveSettings("key_drop_box_storage", enabled)
            _settings.value = _settings.value.copy(dropboxEnabled = enabled)
        }
    }

    fun updateTelegramEnabled(enabled: Boolean) {
        viewModelScope.launch {
            localStorage.saveSettings("key_telegram_storage", enabled)
            _settings.value = _settings.value.copy(telegramEnabled = enabled)
        }
    }

    fun updateTelegramToken(token: String) {
        viewModelScope.launch {
            localStorage.saveSettings("key_telegram_token", token)
            _settings.value = _settings.value.copy(telegramToken = token)
        }
    }

    fun updateTelegramChatId(chatId: String) {
        viewModelScope.launch {
            localStorage.saveSettings("key_telegram_chat_id", chatId)
            _settings.value = _settings.value.copy(telegramChatId = chatId)
        }
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = getApplication<Application>().packageManager.getPackageInfo(
                getApplication<Application>().packageName,
                0
            )
            packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getAppName(): String {
        return try {
            val applicationInfo = getApplication<Application>().applicationInfo
            getApplication<Application>().packageManager.getApplicationLabel(applicationInfo)
                .toString()
        } catch (e: Exception) {
            "App Extractor"
        }
    }
} 