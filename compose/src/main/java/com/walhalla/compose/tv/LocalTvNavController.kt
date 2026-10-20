package com.walhalla.compose.tv

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalTvNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalTvNavController not provided")
}

val LocalTvDrawerFocusRequester = staticCompositionLocalOf<androidx.compose.ui.focus.FocusRequester?> {
    null
}
