package com.walhalla.compose.ui.tv.extractor

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.R
import com.walhalla.compose.detail.AppMenuActionId
import com.walhalla.compose.detail.SortOption
import com.walhalla.compose.detail.sortedByOption
import com.walhalla.compose.tv.LocalTvDrawerFocusRequester
import com.walhalla.compose.tv.LocalTvNavController
import com.walhalla.compose.tv.TvRoutes
import com.walhalla.compose.ui.tv.components.TvAppActionsDialog
import com.walhalla.compose.ui.tv.components.TvEditableField
import com.walhalla.compose.ui.tv.components.TvSortDialog
import com.walhalla.compose.storage.rememberExtractWithStoragePermission
import com.walhalla.compose.ui.tv.components.RestoreFocusOnReturn
import com.walhalla.compose.ui.tv.components.rememberClearFocusThen
import com.walhalla.compose.ui.tv.components.rememberEnterListFocus
import com.walhalla.compose.ui.tv.components.rememberTvLazyFocusRestorer
import com.walhalla.compose.ui.tv.components.tvLazyFocusGroup
import com.walhalla.compose.viewmodel.ExtractorViewModel
import com.walhalla.extractor.R as ExtR
import kotlinx.coroutines.delay

/**
 * TV extractor list: one focus target per row.
 * OK / short press → detail; long-press → actions dialog (phone overflow parity).
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ExtractorScreen(
    extractorViewModel: ExtractorViewModel? = null
) {
    val activity = LocalContext.current as ComponentActivity
    val vm = extractorViewModel ?: viewModel(viewModelStoreOwner = activity)
    val apps by vm.apps.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val nav = LocalTvNavController.current
    val drawerFocus = LocalTvDrawerFocusRequester.current
    val listRestorer = rememberTvLazyFocusRestorer()
    val listState = rememberLazyListState()
    val sortFocus = remember { FocusRequester() }
    val searchFocus = remember { FocusRequester() }
    val clearFocusThen = rememberClearFocusThen()
    var menuApp by remember { mutableStateOf<PackageMeta?>(null) }
    var currentSort by remember { mutableStateOf(SortOption.NAME_ASC) }
    var showSortDialog by remember { mutableStateOf(false) }
    val sortedApps = remember(apps, currentSort) { apps.sortedByOption(currentSort) }
    val enterList = rememberEnterListFocus(listRestorer, listState, sortedApps.size)
    val extractWithStorage = rememberExtractWithStoragePermission(vm::extractApkRequest)

    // Search/filter shrinks the list — old lastFocused index is meaningless.
    LaunchedEffect(sortedApps.size, searchQuery) {
        listRestorer.clampToSize(sortedApps.size)
    }

    listRestorer.RestoreFocusOnReturn(listState, sortedApps.size)

    LaunchedEffect(activity) {
        vm.activityProvider = { activity }
    }

    val leftToDrawer = if (drawerFocus != null) {
        Modifier.focusProperties { left = drawerFocus }
    } else {
        Modifier
    }

    fun openDetail(app: PackageMeta) {
        listRestorer.markRestorePending()
        clearFocusThen {
            nav.currentBackStackEntry?.savedStateHandle?.set("detail_app", app)
            nav.navigate(TvRoutes.appDetail(app.packageName))
        }
    }

    fun openManifest(app: PackageMeta) {
        listRestorer.markRestorePending()
        clearFocusThen {
            nav.currentBackStackEntry?.savedStateHandle?.set("manifest_app", app)
            nav.currentBackStackEntry?.savedStateHandle?.set("manifest_xml", null as String?)
            nav.navigate(TvRoutes.Manifest)
        }
    }

    fun onMenuAction(app: PackageMeta, id: AppMenuActionId) {
        when (id) {
            AppMenuActionId.Extract -> extractWithStorage(app)
            AppMenuActionId.ShareApk -> vm.shareApk(app)
            AppMenuActionId.PlayStore -> vm.openPlayStore(app)
            AppMenuActionId.Launch -> vm.launchApp(app)
            AppMenuActionId.Uninstall -> vm.uninstallApp(app)
            AppMenuActionId.CopyPackage -> vm.copyPackageName(app)
            AppMenuActionId.AppInfo -> openDetail(app)
            AppMenuActionId.Manifest -> openManifest(app)
            AppMenuActionId.SaveIcon -> vm.saveIcon(app)
            AppMenuActionId.ExportIcon -> vm.shareIcon(app)
        }
    }

    menuApp?.let { app ->
        TvAppActionsDialog(
            appLabel = app.label,
            onAction = { onMenuAction(app, it) },
            onDismiss = { menuApp = null }
        )
    }

    if (showSortDialog) {
        TvSortDialog(
            current = currentSort,
            onSelect = { currentSort = it },
            onDismiss = { showSortDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TvEditableField(
                value = searchQuery,
                onValueChange = vm::setSearchQuery,
                hint = stringResource(ExtR.string.search_hint),
                leftFocus = drawerFocus,
                rightFocus = sortFocus,
                onDown = enterList.takeIf { sortedApps.isNotEmpty() },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(searchFocus)
                    .then(leftToDrawer)
            )
            Surface(
                onClick = { showSortDialog = true },
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.08f),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier
                    .size(56.dp)
                    .focusRequester(sortFocus)
                    .focusProperties { left = searchFocus }
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown &&
                            event.key == Key.DirectionDown &&
                            sortedApps.isNotEmpty()
                        ) {
                            enterList()
                            true
                        } else {
                            false
                        }
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = stringResource(R.string.sort),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading && apps.isEmpty() && searchQuery.isBlank() -> {
                    Text(
                        text = stringResource(ExtR.string.dlg_loading_title),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Keep one LazyColumn instance — swapping it out while pinned crashes
                // FocusRestorer ("Release should only be called once").
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .tvLazyFocusGroup(listRestorer)
                            .then(leftToDrawer),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (sortedApps.isEmpty()) {
                            item(key = "empty") {
                                Text(
                                    text = if (searchQuery.isBlank()) {
                                        "—"
                                    } else {
                                        stringResource(ExtR.string.search_no_results, searchQuery)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            itemsIndexed(sortedApps, key = { _, app -> app.packageName }) { index, app ->
                                AppRow(
                                    app = app,
                                    modifier = listRestorer.itemModifier(index, sortedApps.size),
                                    onClick = { openDetail(app) },
                                    onLongClick = { menuApp = app }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AppRow(
    app: PackageMeta,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // FireTv / Cinema: long-press arms a flag; invoke on KeyUp so dialog is not
    // activated by the same KeyUp that opened it.
    var isKeyDown by remember { mutableStateOf(false) }
    var longPressTriggered by remember { mutableStateOf(false) }
    val longPressThresholdMs = 500L

    LaunchedEffect(isKeyDown) {
        if (isKeyDown && !longPressTriggered) {
            delay(longPressThresholdMs)
            longPressTriggered = true
        }
    }

    Surface(
        onClick = {
            if (!longPressTriggered) onClick()
        },
        onLongClick = onLongClick,
        modifier = modifier
            .fillMaxWidth()
            .onPreviewKeyEvent { event ->
                when (event.key) {
                    Key.Enter, Key.DirectionCenter -> {
                        when (event.type) {
                            KeyEventType.KeyDown -> {
                                if (!isKeyDown) {
                                    isKeyDown = true
                                    longPressTriggered = false
                                }
                                true
                            }
                            KeyEventType.KeyUp -> {
                                if (isKeyDown) {
                                    if (longPressTriggered) {
                                        onLongClick()
                                    } else {
                                        onClick()
                                    }
                                }
                                isKeyDown = false
                                longPressTriggered = false
                                true
                            }
                            else -> false
                        }
                    }
                    else -> false
                }
            },
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (app.icon == null) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                AsyncImage(
                    model = app.icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val ver = "v${app.versionName ?: "?"} (${app.versionCode})"
                val size = if (app.fileSize > 0) " · ${prettyFileSize(app.fileSize)}" else ""
                Text(
                    text = ver + size,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
