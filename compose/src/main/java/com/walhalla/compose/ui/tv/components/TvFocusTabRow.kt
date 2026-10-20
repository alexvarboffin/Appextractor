package com.walhalla.compose.ui.tv.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text

/**
 * D-pad: focus on a tab switches content (no OK required). FireTv / Cinema pattern.
 */
@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun TvFocusTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    tabRowFallback: FocusRequester,
    modifier: Modifier = Modifier
) {
    val accent = MaterialTheme.colorScheme.primary
    val selectedPill = MaterialTheme.colorScheme.surfaceVariant
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier
            .focusRestorer { tabRowFallback }
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        indicator = { tabPositions, doesTabRowHaveFocus ->
            tabPositions.getOrNull(selectedIndex)?.let { currentTabPosition ->
                TabRowDefaults.PillIndicator(
                    currentTabPosition = currentTabPosition,
                    doesTabRowHaveFocus = doesTabRowHaveFocus,
                    activeColor = accent,
                    inactiveColor = selectedPill
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onFocus = { onSelectedIndexChange(index) },
                onClick = { onSelectedIndexChange(index) },
                colors = TabDefaults.pillIndicatorTabColors(
                    selectedContentColor = accent,
                    focusedContentColor = Color.White,
                    focusedSelectedContentColor = Color.White
                ),
                modifier = if (index == 0) {
                    Modifier.focusRequester(tabRowFallback)
                } else {
                    Modifier
                }
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}
