package com.walhalla.compose.ui.tv.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged

/**
 * LazyList/LazyGrid focus restore (Cinema / FireTv Case B).
 * Put [tvLazyFocusGroup] on the Lazy* itself — not a nested focusGroup.
 *
 * Compose 1.7: [focusRestorer] takes `(() -> FocusRequester)?` (ExperimentalComposeUiApi).
 */
@Stable
class TvLazyFocusRestorer internal constructor(
    val group: FocusRequester,
    val child: FocusRequester,
    private val lastFocused: MutableIntState,
) {
    fun childModifier(): Modifier = Modifier.focusRequester(child)

    fun itemModifier(index: Int, itemCount: Int): Modifier {
        val target = lastFocused.intValue.coerceIn(0, (itemCount - 1).coerceAtLeast(0))
        return (if (index == target) childModifier() else Modifier)
            .onFocusChanged { if (it.isFocused) lastFocused.intValue = index }
    }
}

@Composable
fun rememberTvLazyFocusRestorer(): TvLazyFocusRestorer {
    val group = remember { FocusRequester() }
    val child = remember { FocusRequester() }
    val lastFocused = rememberSaveable { mutableIntStateOf(0) }
    return remember {
        TvLazyFocusRestorer(group = group, child = child, lastFocused = lastFocused)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.tvLazyFocusGroup(restorer: TvLazyFocusRestorer): Modifier =
    focusRequester(restorer.group).focusRestorer { restorer.child }
