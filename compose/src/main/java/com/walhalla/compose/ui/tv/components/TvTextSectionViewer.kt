package com.walhalla.compose.ui.tv.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.walhalla.compose.R
import kotlinx.coroutines.launch

/**
 * One D-pad step moves a whole page, the same idea as Cinema `VerticalPager` sections.
 * Pages are packed by how many wrapped rows fit, not by a fixed number of source lines.
 */
@Composable
fun TvTextSectionViewer(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val focus = remember { FocusRequester() }

    BoxWithConstraints(modifier) {
        val charsPerLine = (maxWidth / CharWidth).toInt().coerceIn(16, 160)
        val rowsPerPage = (maxHeight / RowHeight).toInt().coerceIn(6, 48)
        val pages = remember(text, charsPerLine, rowsPerPage) {
            splitTextSections(text, charsPerLine, rowsPerPage)
        }
        val pagerState = rememberPagerState(pageCount = { pages.size })

        LaunchedEffect(pages.size) {
            runCatching { focus.requestFocus() }
        }

        Row(Modifier.fillMaxSize()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .focusRequester(focus)
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                        when (event.key) {
                            Key.DirectionDown -> {
                                if (pagerState.currentPage < pages.lastIndex) {
                                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                    true
                                } else {
                                    false
                                }
                            }
                            Key.DirectionUp -> {
                                if (pagerState.currentPage > 0) {
                                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                    true
                                } else {
                                    false
                                }
                            }
                            else -> false
                        }
                    },
                pageSize = PageSize.Fill,
                beyondViewportPageCount = 1,
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    pagerSnapDistance = PagerSnapDistance.atMost(1)
                )
            ) { index ->
                Text(
                    text = pages[index],
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 8.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = colors.onSurface
                )
            }
            Column(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.section_page, pagerState.currentPage + 1, pages.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
                if (pages.size in 2..12) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        repeat(pages.size) { index ->
                            val active = index == pagerState.currentPage
                            Box(
                                Modifier
                                    .size(if (active) 10.dp else 6.dp)
                                    .background(
                                        if (active) colors.primary else colors.onSurface.copy(alpha = 0.35f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

private val CharWidth = 7.2.dp
private val RowHeight = 18.dp

/**
 * Packs [text] into pages of at most [rowsPerPage] visual rows.
 * A source line wider than [charsPerLine] wraps and can continue on the next page.
 */
fun splitTextSections(text: String, charsPerLine: Int, rowsPerPage: Int): List<String> {
    val width = charsPerLine.coerceAtLeast(8)
    val height = rowsPerPage.coerceAtLeast(1)
    if (text.isEmpty()) return listOf("")
    val pages = mutableListOf(StringBuilder())
    var usedRows = 0
    fun newPage() {
        pages += StringBuilder()
        usedRows = 0
    }
    for (raw in text.split('\n')) {
        var rest = raw
        while (true) {
            if (usedRows >= height) newPage()
            val capacity = (height - usedRows) * width
            if (rest.length <= capacity) {
                val cost = if (rest.isEmpty()) 1 else (rest.length + width - 1) / width
                if (pages.last().isNotEmpty()) pages.last().append('\n')
                pages.last().append(rest)
                usedRows += cost
                break
            }
            if (pages.last().isNotEmpty()) pages.last().append('\n')
            pages.last().append(rest.take(capacity))
            rest = rest.drop(capacity)
            newPage()
        }
    }
    return pages.map { it.toString() }
}
