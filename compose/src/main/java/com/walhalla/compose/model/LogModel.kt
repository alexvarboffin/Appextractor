package com.walhalla.compose.model

import androidx.annotation.DrawableRes

sealed class LogModel {
    data class FileLog(
        val filePath: String,
        val fileName: String,
        @DrawableRes val icon: Int,
        val size: Long,
        val lastModified: Long
    ) : LogModel()

    data class MessageLog(
        val message: String,
        @DrawableRes val icon: Int,
        val type: LogType,
        val timestamp: Long = System.currentTimeMillis()
    ) : LogModel()
}

enum class LogType(val id: Int) {
    Empty(0),
    Info(1),
    Warning(2),
    Error(3),
    Success(4)
} 