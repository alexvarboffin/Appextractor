package com.walhalla.compose.ui.tv.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.walhalla.compose.R
import com.walhalla.compose.detail.AppFilter
import com.walhalla.compose.detail.SortOption
import com.walhalla.compose.detail.sortMenuItems

@Composable
fun TvSortDialog(
    current: SortOption,
    onSelect: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    val items = remember { sortMenuItems() }
    TvChoiceDialog(
        title = stringResource(R.string.sort),
        choices = items.map {
            TvChoice(stringResource(it.labelRes), it.icon, selected = it.option == current)
        },
        onSelect = { onSelect(items[it].option) },
        onDismiss = onDismiss
    )
}

@Composable
fun TvFilterDialog(
    current: AppFilter,
    onSelect: (AppFilter) -> Unit,
    onDismiss: () -> Unit
) {
    val items = AppFilter.entries
    TvChoiceDialog(
        title = stringResource(R.string.filter),
        choices = items.map {
            TvChoice(stringResource(it.labelRes), it.icon, selected = it == current)
        },
        onSelect = { onSelect(items[it]) },
        onDismiss = onDismiss
    )
}
