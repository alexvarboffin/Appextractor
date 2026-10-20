package com.walhalla.compose.ui.tv.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch

/**
 * Clears focus (unpins Lazy [focusRestorer] children) then runs [action] on the next frame.
 *
 * Required before removing a Lazy* with [tvLazyFocusGroup] from composition — otherwise
 * [FocusRestorerNode.onDetach] + [LazyLayoutPinnableItem.release] crash with
 * "Release should only be called once" (nav to detail, drawer panel switch, Back).
 */
@Composable
fun rememberClearFocusThen(): (action: () -> Unit) -> Unit {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    return remember(focusManager, scope) {
        { action ->
            focusManager.clearFocus(force = true)
            scope.launch {
                withFrameNanos { }
                action()
            }
        }
    }
}
