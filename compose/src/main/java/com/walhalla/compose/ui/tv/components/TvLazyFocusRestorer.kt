package com.walhalla.compose.ui.tv.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch

/**
 * LazyList/LazyGrid focus — FireTv Case B, adapted for Compose BOM where
 * `focusRestorer { child }` + D-pad enter crashes if [child] is not composed
 * ("FocusRequester is not initialized" via TwoDimensionalFocusSearch).
 *
 * - Lazy*: [focusRequester]([group]) + [focusRestorer]() **without** fallback FR.
 * - Enter from search: [rememberEnterListFocus] — clamp index, wait for composition,
 *   then [child].requestFocus().
 * - After search/filter, call [clampToSize] so last index is valid for the new list.
 *
 * Do **not** put [group] in `focusProperties { down/left = group }`.
 */
@Stable
class TvLazyFocusRestorer internal constructor(
    val group: FocusRequester,
    val child: FocusRequester,
    private val lastFocused: MutableIntState,
    internal var restorePending: Boolean,
) {
    val lastFocusedIndex: Int get() = lastFocused.intValue

    fun childModifier(): Modifier = Modifier.focusRequester(child)

    fun itemModifier(index: Int, itemCount: Int): Modifier {
        if (itemCount <= 0) return Modifier
        val target = lastFocused.intValue.coerceIn(0, itemCount - 1)
        return (if (index == target) childModifier() else Modifier)
            .onFocusChanged { if (it.isFocused) lastFocused.intValue = index }
    }

    /** Keep saved index inside the current list (search/filter shrink). */
    fun clampToSize(itemCount: Int) {
        if (itemCount <= 0) {
            lastFocused.intValue = -1
            return
        }
        if (lastFocused.intValue !in 0 until itemCount) {
            lastFocused.intValue = 0
        }
    }

    /**
     * Pick enter index and write it so [itemModifier] attaches [child] on next frame.
     * Invalid / unset last focus → 0 (first row after search).
     */
    fun prepareEnterIndex(itemCount: Int): Int {
        if (itemCount <= 0) return -1
        val idx = if (lastFocused.intValue in 0 until itemCount) lastFocused.intValue else 0
        lastFocused.intValue = idx
        return idx
    }

    fun markRestorePending() {
        if (lastFocused.intValue >= 0) restorePending = true
    }
}

@Composable
fun rememberTvLazyFocusRestorer(): TvLazyFocusRestorer {
    val group = remember { FocusRequester() }
    val child = remember { FocusRequester() }
    val lastFocused = rememberSaveable { mutableIntStateOf(-1) }
    return remember {
        TvLazyFocusRestorer(
            group = group,
            child = child,
            lastFocused = lastFocused,
            restorePending = lastFocused.intValue >= 0,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.tvLazyFocusGroup(restorer: TvLazyFocusRestorer): Modifier =
    focusRequester(restorer.group).focusRestorer()

/**
 * Scroll to enter index (so [TvLazyFocusRestorer.child] is composed), then focus it.
 */
@Composable
fun rememberEnterListFocus(
    restorer: TvLazyFocusRestorer,
    state: LazyListState,
    itemCount: Int,
    indexOffset: Int = 0,
): () -> Unit {
    val scope = rememberCoroutineScope()
    return remember(restorer, state, itemCount, indexOffset, scope) {
        {
            if (itemCount > 0) {
                scope.launch {
                    val target = restorer.prepareEnterIndex(itemCount)
                    if (target < 0) return@launch
                    val lazyIndex = target + indexOffset
                    val visible = state.layoutInfo.visibleItemsInfo.any { it.index == lazyIndex }
                    if (!visible) {
                        state.scrollToItem(lazyIndex)
                    }
                    // Wait for itemModifier to put [child] on the target row.
                    withFrameNanos { }
                    withFrameNanos { }
                    val focused = runCatching { restorer.child.requestFocus() }.isSuccess
                    if (!focused) {
                        runCatching { restorer.group.requestFocus() }
                    }
                }
            }
        }
    }
}

@Composable
fun TvLazyFocusRestorer.RestoreFocusOnReturn(
    state: LazyListState,
    itemCount: Int,
    indexOffset: Int = 0,
) = RestoreFocusOnReturn(
    itemCount = itemCount,
    isVisible = { index ->
        state.layoutInfo.visibleItemsInfo.any { it.index == index + indexOffset }
    },
    scrollTo = { state.scrollToItem(it + indexOffset) },
)

@Composable
fun TvLazyFocusRestorer.RestoreFocusOnReturn(
    state: LazyGridState,
    itemCount: Int,
    indexOffset: Int = 0,
) = RestoreFocusOnReturn(
    itemCount = itemCount,
    isVisible = { index ->
        state.layoutInfo.visibleItemsInfo.any { it.index == index + indexOffset }
    },
    scrollTo = { state.scrollToItem(it + indexOffset) },
)

@Composable
private fun TvLazyFocusRestorer.RestoreFocusOnReturn(
    itemCount: Int,
    isVisible: (Int) -> Boolean,
    scrollTo: suspend (Int) -> Unit,
) {
    LaunchedEffect(itemCount > 0) {
        if (!restorePending || itemCount == 0) return@LaunchedEffect
        restorePending = false
        val target = prepareEnterIndex(itemCount)
        if (target < 0) return@LaunchedEffect
        withFrameNanos { }
        if (!isVisible(target)) scrollTo(target)
        withFrameNanos { }
        withFrameNanos { }
        runCatching { child.requestFocus() }
    }
}
