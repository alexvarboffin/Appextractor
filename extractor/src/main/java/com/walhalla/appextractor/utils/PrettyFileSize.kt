package com.walhalla.appextractor.utils

fun prettyFileSize(longsize: Long): String {
    val size = when {
        longsize > 1024 && longsize <= 1024 * 1024 ->
            "${longsize / 1024} KB"

        longsize > 1024 * 1024 && longsize <= 1024 * 1024 * 1024 ->
            "${longsize / (1024 * 1024)} MB"

        else ->
            "${longsize / (1024 * 1024 * 1024)} GB"
    }
    return size
}