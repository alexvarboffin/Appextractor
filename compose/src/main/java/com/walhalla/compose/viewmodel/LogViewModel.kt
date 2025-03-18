package com.walhalla.compose.viewmodel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.compose.model.LogModel
import com.walhalla.compose.model.LogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class LogViewModel(application: Application) : AndroidViewModel(application) {
    private val _logs = MutableStateFlow<List<LogModel>>(emptyList())
    val logs: StateFlow<List<LogModel>> = _logs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLogs()
    }

    private fun loadLogs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val extractedApksDir = File(downloadDir, "ExtractedApks")
                
                if (!extractedApksDir.exists() || !extractedApksDir.isDirectory) {
                    _logs.value = listOf(
                        LogModel.MessageLog(
                            message = "No extracted APKs found",
                            icon = android.R.drawable.ic_dialog_info,
                            type = LogType.Empty
                        )
                    )
                    return@launch
                }

                val files = extractedApksDir.listFiles { file ->
                    file.name.endsWith(".apk")
                }

                val logsList = files?.map { file ->
                    LogModel.FileLog(
                        filePath = file.absolutePath,
                        fileName = file.name,
                        icon = android.R.drawable.ic_menu_save,
                        size = file.length(),
                        lastModified = file.lastModified()
                    )
                } ?: emptyList()

                _logs.value = if (logsList.isEmpty()) {
                    listOf(
                        LogModel.MessageLog(
                            message = "No extracted APKs found",
                            icon = android.R.drawable.ic_dialog_info,
                            type = LogType.Empty
                        )
                    )
                } else {
                    logsList
                }
            } catch (e: Exception) {
                _logs.value = listOf(
                    LogModel.MessageLog(
                        message = "Error loading logs: ${e.message}",
                        icon = android.R.drawable.ic_dialog_alert,
                        type = LogType.Error
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            try {
                val file = File(filePath)
                if (file.exists() && file.delete()) {
                    addLog(
                        LogModel.MessageLog(
                            message = "File deleted: ${file.name}",
                            icon = android.R.drawable.ic_dialog_info,
                            type = LogType.Success
                        )
                    )
                    loadLogs()
                } else {
                    addLog(
                        LogModel.MessageLog(
                            message = "Failed to delete file: ${file.name}",
                            icon = android.R.drawable.ic_dialog_alert,
                            type = LogType.Error
                        )
                    )
                }
            } catch (e: Exception) {
                addLog(
                    LogModel.MessageLog(
                        message = "Error deleting file: ${e.message}",
                        icon = android.R.drawable.ic_dialog_alert,
                        type = LogType.Error
                    )
                )
            }
        }
    }

    private fun addLog(log: LogModel) {
        _logs.value = _logs.value + log
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun refresh() {
        loadLogs()
    }
} 