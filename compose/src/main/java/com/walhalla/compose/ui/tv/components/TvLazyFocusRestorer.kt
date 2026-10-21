package com.walhalla.compose.ui.tv.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Cinema list: `focusRequester(parent).focusRestorer(firstItem)`.
 * [firstItem] is only the fallback, attached to the first child
 * ([CategoryScreenTv] `Position.FirstChild`). The previously focused row
 * is stored by [focusRestorer] itself — no index.
 */
@Stable
class TvLazyFocusRestorer internal constructor(
    val parent: FocusRequester,
    val firstItem: FocusRequester,
) {
    fun firstItemModifier(): Modifier = Modifier.focusRequester(firstItem)
}

@Composable
fun rememberTvLazyFocusRestorer(): TvLazyFocusRestorer {
    val parent = remember { FocusRequester() }
    val firstItem = remember { FocusRequester() }
    return remember(parent, firstItem) { TvLazyFocusRestorer(parent, firstItem) }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.tvLazyFocusGroup(restorer: TvLazyFocusRestorer): Modifier =
    focusRequester(restorer.parent).focusRestorer(restorer.firstItem)

/** Throw focus onto the list. [focusRestorer] picks the child. */
@Composable
fun rememberEnterListFocus(restorer: TvLazyFocusRestorer): () -> Unit {
    val focusManager = LocalFocusManager.current
    return remember(restorer, focusManager) {
        {
            focusManager.clearFocus(force = true)
            runCatching { restorer.parent.requestFocus() }
        }
    }
}
