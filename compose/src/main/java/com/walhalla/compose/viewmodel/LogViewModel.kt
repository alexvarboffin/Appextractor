package com.walhalla.compose.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.Config
import com.walhalla.appextractor.Troubleshooting
import com.walhalla.compose.log.AppLogBus
import com.walhalla.compose.model.LogModel
import com.walhalla.compose.model.LogType
import com.walhalla.extractor.R as ExtR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LogViewModel(application: Application) : AndroidViewModel(application) {
    private val _fileLogs = MutableStateFlow<List<LogModel.FileLog>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _logs = MutableStateFlow<List<LogModel>>(emptyList())
    val logs: StateFlow<List<LogModel>> = _logs.asStateFlow()

    init {
        viewModelScope.launch {
            combine(AppLogBus.messages, _fileLogs) { messages, files ->
                when {
                    messages.isEmpty() && files.isEmpty() -> listOf(emptyPlaceholder())
                    else -> messages + files
                }
            }.collect { _logs.value = it }
        }
        loadLogs()
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun emptyPlaceholder(): LogModel.MessageLog {
        return LogModel.MessageLog(
            message = getApplication<Application>().getString(ExtR.string.data_empty),
            icon = ExtR.drawable.ic_log_file_bg,
            type = LogType.Empty
        )
    }

    fun loadLogs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _fileLogs.value = withContext(Dispatchers.IO) { scanExtractedApks() }
            } catch (e: Exception) {
                AppLogBus.error(e.message ?: "Error loading logs")
                _fileLogs.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Compose ExtractedApks (recursive) + Downloads root / Apk_Chief_* (legacy)
    private fun scanExtractedApks(): List<LogModel.FileLog> {
        val found = LinkedHashMap<String, File>()

        val downloads = Troubleshooting.defLocation()
        val composeOut = File(downloads, COMPOSE_EXTRACT_DIR)
        if (composeOut.exists()) {
            composeOut.walkTopDown()
                .filter { it.isFile && it.extension.equals("apk", ignoreCase = true) }
                .forEach { found[it.absolutePath] = it }
        }

        downloads.listFiles()?.forEach { entry ->
            when {
                entry.isFile && entry.name.endsWith(".apk", ignoreCase = true) -> {
                    found[entry.absolutePath] = entry
                }
                entry.isDirectory && entry.name.startsWith(Config.PREV) -> {
                    entry.walkTopDown()
                        .filter { it.isFile && it.extension.equals("apk", ignoreCase = true) }
                        .forEach { found[it.absolutePath] = it }
                }
            }
        }

        return found.values
            .sortedByDescending { it.lastModified() }
            .map { file ->
                LogModel.FileLog(
                    filePath = file.absolutePath,
                    fileName = file.name,
                    icon = ExtR.drawable.ic_log_file_bg,
                    size = file.length(),
                    lastModified = file.lastModified()
                )
            }
    }

    fun deleteFile(filePath: String) {
        viewModelScope.launch {
            try {
                val file = File(filePath)
                val deleted = withContext(Dispatchers.IO) {
                    file.exists() && file.delete()
                }
                if (deleted) {
                    _toastMessage.value = "Success!!!"
                    AppLogBus.success("File deleted: ${file.name}")
                    loadLogs()
                } else {
                    AppLogBus.error("Failed to delete file: ${file.name}")
                }
            } catch (e: Exception) {
                AppLogBus.error("Error deleting file: ${e.message}")
            }
        }
    }

    /** Clears session messages only; reloads files from disk (does not delete APKs). */
    fun clearSessionMessages() {
        AppLogBus.clear()
        loadLogs()
    }

    fun refresh() {
        loadLogs()
    }

    companion object {
        const val COMPOSE_EXTRACT_DIR = "ExtractedApks"
    }
}
