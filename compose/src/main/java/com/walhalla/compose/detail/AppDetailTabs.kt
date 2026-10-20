package com.walhalla.compose.detail

/** Shared tab order for phone + TV app detail (parity). */
object AppDetailTabs {
    const val INFO = 0
    const val META = 1
    const val STRING = 2
    const val XML = 3
    const val ASSETS = 4

    val titles: List<String> = listOf("INFO", "META", "STRING", "XML", "ASSETS")
}
