package com.walhalla.compose.log

import com.walhalla.compose.model.LogModel
import com.walhalla.compose.model.LogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Session log messages shared between Extractor and Logs
 * (parity with legacy MainActivity.printOutput → LogFragment.makeLog).
 * StateFlow so events are kept even if Logs screen was never opened yet.
 */
object AppLogBus {
    private val _messages = MutableStateFlow<List<LogModel.MessageLog>>(emptyList())
    val messages: StateFlow<List<LogModel.MessageLog>> = _messages.asStateFlow()

    fun success(message: String) {
        prepend(
            LogModel.MessageLog(
                message = message,
                icon = android.R.drawable.ic_dialog_info,
                type = LogType.Success
            )
        )
    }

    fun error(message: String) {
        prepend(
            LogModel.MessageLog(
                message = message,
                icon = android.R.drawable.ic_dialog_alert,
                type = LogType.Error
            )
        )
    }

    fun clear() {
        _messages.value = emptyList()
    }

    private fun prepend(entry: LogModel.MessageLog) {
        _messages.update { listOf(entry) + it }
    }
}
