package com.walhalla.compose.ui.tv.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterState
import com.dokar.sonner.rememberToasterState
import com.dokar.sonner.ToastType
import com.walhalla.compose.log.AppLogBus
import com.walhalla.compose.model.LogType

val LocalTvToaster = staticCompositionLocalOf<ToasterState> {
    error("LocalTvToaster not provided — wrap TV root with TvToasterHost")
}

/**
 * TV feedback: [compose-sonner](https://github.com/dokar3/compose-sonner) at top-center
 * instead of [android.widget.Toast]. Listens to [AppLogBus.toasts].
 */
@Composable
fun TvToasterHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val toaster = rememberToasterState()

    LaunchedEffect(toaster) {
        AppLogBus.toasts.collect { event ->
            toaster.show(
                message = event.message,
                type = when (event.type) {
                    LogType.Success -> ToastType.Success
                    LogType.Error -> ToastType.Error
                    LogType.Warning -> ToastType.Warning
                    LogType.Info, LogType.Empty -> ToastType.Info
                }
            )
        }
    }

    CompositionLocalProvider(LocalTvToaster provides toaster) {
        content()
        Toaster(
            state = toaster,
            modifier = modifier,
            alignment = Alignment.TopCenter,
            darkTheme = true,
            richColors = true,
            maxVisibleToasts = 3
        )
    }
}
