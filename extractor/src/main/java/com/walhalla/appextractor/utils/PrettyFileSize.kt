package com.walhalla.appextractor.utils

fun prettyFileSize(longSize: Long): String {
    val size = when {
        longSize > 1024 && longSize <= 1024 * 1024 ->
            "${longSize / 1024} KB"

        longSize > 1024 * 1024 && longSize <= 1024 * 1024 * 1024 ->
            "${longSize / (1024 * 1024)} MB"

        else ->
            "${longSize / (1024 * 1024 * 1024)} GB"
    }
    return size
}