package com.walhalla.compose.ui.tv.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch

/**
 * Cinema [EpisodeCardTV]: a focused Lazy item is scrolled only until its top
 * edge is visible, so a tall card stays clipped. [BringIntoViewRequester] asks
 * the parent to show the whole node.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.tvBringIntoViewOnFocus(): Modifier {
    val requester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    return this
        .bringIntoViewRequester(requester)
        .onFocusChanged { state ->
            if (state.isFocused) {
                scope.launch { runCatching { requester.bringIntoView() } }
            }
        }
}
