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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
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
import com.walhalla.compose.detail.AppMenuActionId
import com.walhalla.compose.tv.LocalTvDrawerFocusRequester
import com.walhalla.compose.tv.LocalTvNavController
import com.walhalla.compose.tv.TvRoutes
import com.walhalla.compose.ui.tv.components.TvAppActionsDialog
import com.walhalla.compose.ui.tv.components.TvEditableField
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
    var menuApp by remember { mutableStateOf<PackageMeta?>(null) }

    LaunchedEffect(activity) {
        vm.activityProvider = { activity }
    }

    val leftToDrawer = if (drawerFocus != null) {
        Modifier.focusProperties { left = drawerFocus }
    } else {
        Modifier
    }

    fun openDetail(app: PackageMeta) {
        nav.currentBackStackEntry?.savedStateHandle?.set("detail_app", app)
        nav.navigate(TvRoutes.appDetail(app.packageName))
    }

    fun openManifest(app: PackageMeta) {
        nav.currentBackStackEntry?.savedStateHandle?.set("manifest_app", app)
        nav.currentBackStackEntry?.savedStateHandle?.set("manifest_xml", null as String?)
        nav.navigate(TvRoutes.Manifest)
    }

    fun onMenuAction(app: PackageMeta, id: AppMenuActionId) {
        when (id) {
            AppMenuActionId.Extract -> vm.extractApkRequest(app)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        TvEditableField(
            value = searchQuery,
            onValueChange = vm::setSearchQuery,
            hint = stringResource(ExtR.string.search_hint),
            leftFocus = drawerFocus,
            downFocus = listRestorer.group.takeIf { apps.isNotEmpty() },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .then(leftToDrawer)
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
                apps.isEmpty() -> {
                    Text(
                        text = if (searchQuery.isBlank()) {
                            "—"
                        } else {
                            stringResource(ExtR.string.search_no_results, searchQuery)
                        },
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .tvLazyFocusGroup(listRestorer)
                            .then(leftToDrawer),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                            AppRow(
                                app = app,
                                modifier = listRestorer.itemModifier(index, apps.size),
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
