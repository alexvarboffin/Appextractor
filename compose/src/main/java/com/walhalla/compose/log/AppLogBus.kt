package com.walhalla.compose.log

import com.walhalla.compose.model.LogModel
import com.walhalla.compose.model.LogType
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Session log messages shared between Extractor and Logs
 * (parity with legacy MainActivity.printOutput → LogFragment.makeLog).
 * StateFlow so events are kept even if Logs screen was never opened yet.
 *
 * [toasts] — ephemeral UI feedback (TV sonner / phone snackbar consumers).
 */
object AppLogBus {
    private val _messages = MutableStateFlow<List<LogModel.MessageLog>>(emptyList())
    val messages: StateFlow<List<LogModel.MessageLog>> = _messages.asStateFlow()

    private val _toasts = MutableSharedFlow<ToastEvent>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val toasts: SharedFlow<ToastEvent> = _toasts.asSharedFlow()

    data class ToastEvent(val message: String, val type: LogType)

    fun success(message: String) {
        prepend(
            LogModel.MessageLog(
                message = message,
                icon = android.R.drawable.ic_dialog_info,
                type = LogType.Success
            )
        )
        _toasts.tryEmit(ToastEvent(message, LogType.Success))
    }

    fun error(message: String) {
        prepend(
            LogModel.MessageLog(
                message = message,
                icon = android.R.drawable.ic_dialog_alert,
                type = LogType.Error
            )
        )
        _toasts.tryEmit(ToastEvent(message, LogType.Error))
    }

    fun info(message: String) {
        prepend(
            LogModel.MessageLog(
                message = message,
                icon = android.R.drawable.ic_dialog_info,
                type = LogType.Info
            )
        )
        _toasts.tryEmit(ToastEvent(message, LogType.Info))
    }

    fun clear() {
        _messages.value = emptyList()
    }

    private fun prepend(entry: LogModel.MessageLog) {
        _messages.update { listOf(entry) + it }
    }
}
