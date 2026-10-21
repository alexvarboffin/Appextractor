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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.tv.material3.Button
import com.walhalla.compose.detail.AppFilter
import com.walhalla.compose.detail.highlightMatches
import com.walhalla.compose.detail.totals
import com.walhalla.compose.storage.rememberStorageGate
import com.walhalla.compose.transfer.appSharedFiles
import com.walhalla.compose.transfer.rememberQrTransfer
import com.walhalla.compose.ui.tv.components.TvFilterDialog
import com.walhalla.compose.ui.tv.components.TvQrTransferDialog
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
import androidx.compose.ui.focus.focusRestorer
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
    val filter by vm.filter.collectAsState()
    val traits by vm.traits.collectAsState()
    val selectedApps by vm.selectedApps.collectAsState()
    val context = LocalContext.current
    val qrTransfer = rememberQrTransfer()
    val storageGate = rememberStorageGate()
    var showFilterDialog by remember { mutableStateOf(false) }
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
    val enterList = rememberEnterListFocus(listRestorer)
    val extractWithStorage = rememberExtractWithStoragePermission(vm::extractApkRequest)

    LaunchedEffect(activity) {
        vm.activityProvider = { activity }
    }

    val leftToDrawer = if (drawerFocus != null) {
        Modifier.focusProperties { left = drawerFocus }
    } else {
        Modifier
    }

    fun openDetail(app: PackageMeta) {
        clearFocusThen {
            nav.currentBackStackEntry?.savedStateHandle?.set("detail_app", app)
            nav.navigate(TvRoutes.appDetail(app.packageName))
        }
    }

    fun openManifest(app: PackageMeta) {
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
            AppMenuActionId.SendQr -> qrTransfer.start(app.label, appSharedFiles(context, app))
        }
    }

    menuApp?.let { app ->
        TvAppActionsDialog(
            appLabel = app.label,
            onAction = { onMenuAction(app, it) },
            onDismiss = { menuApp = null },
            isSelected = app in selectedApps,
            onToggleSelected = { vm.toggleAppSelection(app) }
        )
    }

    if (showSortDialog) {
        TvSortDialog(
            current = currentSort,
            onSelect = { currentSort = it },
            onDismiss = { showSortDialog = false }
        )
    }

    if (showFilterDialog) {
        TvFilterDialog(
            current = filter,
            onSelect = vm::setFilter,
            onDismiss = { showFilterDialog = false }
        )
    }

    TvQrTransferDialog(qrTransfer)

    val downToList = Modifier.onPreviewKeyEvent { event ->
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
    val totals = remember(apps) { apps.totals() }
    val selectionMode = selectedApps.isNotEmpty()

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
            HeaderIconButton(
                icon = Icons.Default.Sort,
                contentDescription = stringResource(R.string.sort),
                onClick = { showSortDialog = true },
                modifier = Modifier
                    .focusRequester(sortFocus)
                    .focusProperties { left = searchFocus }
                    .then(downToList)
            )
            HeaderIconButton(
                icon = filter.icon,
                contentDescription = stringResource(R.string.filter),
                highlighted = filter != AppFilter.ALL,
                onClick = { showFilterDialog = true },
                modifier = downToList
            )
            if (selectionMode) {
                Button(
                    onClick = { storageGate { vm.extractSelectedApps() } },
                    modifier = downToList
                ) {
                    Icon(Icons.Default.SaveAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("${stringResource(ExtR.string.action_extract_btn)} (${selectedApps.size})")
                }
                Button(onClick = vm::clearSelection, modifier = downToList) {
                    Text(stringResource(ExtR.string.action_deselect))
                }
            }
        }

        Text(
            text = buildString {
                append(stringResource(filter.labelRes))
                append(" · ")
                append(stringResource(R.string.apps_totals, totals.count, prettyFileSize(totals.bytes)))
                if (selectionMode) {
                    append(" · ")
                    append(stringResource(R.string.selected_count, selectedApps.size))
                }
            },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )

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



                    LazyColumn(state = listState,
                        modifier = Modifier.fillMaxSize()
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
                                    query = searchQuery.trim(),
                                    selected = app in selectedApps,
                                    tvLauncher = traits[app.packageName]?.tvLauncher == true,
                                    modifier = if (index == 0) listRestorer.firstItemModifier() else Modifier,
                                    onClick = {
                                        if (selectionMode) vm.toggleAppSelection(app) else openDetail(app)
                                    },
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
private fun HeaderIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.08f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (highlighted) colors.primaryContainer else colors.surfaceVariant,
            focusedContainerColor = colors.secondaryContainer
        ),
        modifier = modifier.size(56.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (highlighted) colors.onPrimaryContainer else colors.onSurface
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun AppRow(
    app: PackageMeta,
    query: String,
    selected: Boolean,
    tvLauncher: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // FireTv / Cinema: long-press arms a flag; invoke on KeyUp so dialog is not
    // activated by the same KeyUp that opened it.
    val colors = MaterialTheme.colorScheme
    val highlightedLabel = remember(app.label, query, colors.primary) {
        highlightMatches(app.label, query, colors.primary)
    }
    val highlightedPackage = remember(app.packageName, query, colors.primary) {
        highlightMatches(app.packageName, query, colors.primary)
    }
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
            containerColor = if (selected) colors.primaryContainer else colors.surface,
            focusedContainerColor = if (selected) colors.secondaryContainer else colors.surfaceVariant
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
                    text = highlightedLabel,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = highlightedPackage,
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
            if (tvLauncher) {
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = stringResource(R.string.insight_tv_ready),
                    tint = colors.tertiary,
                    modifier = Modifier.size(22.dp)
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
