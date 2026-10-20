package com.walhalla.compose.ui.tv.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.LauncherUtils
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.detail.InfoLineAction
import com.walhalla.compose.detail.InfoLineStatus
import com.walhalla.compose.detail.InfoLineUi
import com.walhalla.compose.detail.groupIntoSections
import com.walhalla.compose.detail.mapInfoLine
import com.walhalla.compose.ui.tv.components.TvActionHintIcon
import com.walhalla.compose.viewmodel.AppDetails
import com.walhalla.extractor.R as ExtR

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvInformationTab(
    app: PackageMeta,
    appDetails: AppDetails?,
    onLaunchService: (String) -> Unit,
    onOpenAppSettings: () -> Unit,
    onViewXml: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sections = remember(appDetails?.infoLines, app.packageName) {
        groupIntoSections(appDetails?.infoLines.orEmpty(), app)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (appDetails == null) {
            item {
                TvKeyValueBlock(
                    title = "Basic Information",
                    rows = listOf(
                        "Package Name" to app.packageName,
                        "Version" to "${app.versionName} (${app.versionCode})",
                        "Size" to prettyFileSize(app.fileSize),
                        "Has splits" to if (app.hasSplits) "Yes" else "No",
                        "Pinning" to if (app.hasPinning) "Yes" else "No",
                        "System App" to if (app.isSystemApp) "Yes" else "No"
                    )
                )
            }
        } else {
            sections.forEach { section ->
                item(key = section.title + section.items.size) {
                    var expanded by remember(section.title) {
                        mutableStateOf(section.title.contains("Basic", ignoreCase = true))
                    }
                    TvSectionSurface(
                        title = section.title.ifBlank { "Details" },
                        expanded = expanded,
                        onToggle = { expanded = !expanded }
                    ) {
                        if (expanded) {
                            section.items.forEach { line ->
                                mapInfoLine(context, line, app.packageName)?.let { ui ->
                                    TvInfoLineRow(
                                        ui = ui,
                                        packageName = app.packageName,
                                        onLaunchService = onLaunchService,
                                        onOpenAppSettings = onOpenAppSettings,
                                        onViewXml = onViewXml
                                    )
                                }
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
private fun TvSectionSurface(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            onClick = onToggle,
            modifier = Modifier.fillMaxWidth(),
            colors = ClickableSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = if (expanded) "▾ $title" else "▸ $title",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        content()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvInfoLineRow(
    ui: InfoLineUi,
    packageName: String,
    onLaunchService: (String) -> Unit,
    onOpenAppSettings: () -> Unit,
    onViewXml: (String) -> Unit
) {
    val context = LocalContext.current
    var childrenOpen by remember { mutableStateOf(ui.children.isNotEmpty()) }
    var focused by remember { mutableStateOf(false) }

    val clickable = ui.action != InfoLineAction.None || ui.children.isNotEmpty()
    val hint = infoActionHint(ui)
    if (clickable) {
        Surface(
            onClick = {
                when {
                    ui.children.isNotEmpty() -> childrenOpen = !childrenOpen
                    else -> dispatchInfoAction(
                        context, ui, packageName, onLaunchService, onOpenAppSettings, onViewXml
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .onFocusChanged { focused = it.isFocused },
            colors = ClickableSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoLineContent(ui, Modifier.weight(1f))
                if (hint != null) {
                    TvActionHintIcon(
                        icon = hint.icon,
                        focused = focused,
                        contentDescription = hint.labelRes?.let { stringResource(it) },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    } else {
        InfoLineContent(
            ui,
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }

    if (childrenOpen && ui.children.isNotEmpty()) {
        Column(modifier = Modifier.padding(start = 12.dp)) {
            ui.children.forEach { child ->
                TvInfoLineRow(
                    ui = child,
                    packageName = packageName,
                    onLaunchService = onLaunchService,
                    onOpenAppSettings = onOpenAppSettings,
                    onViewXml = onViewXml
                )
            }
        }
    }
}

private data class ActionHint(
    val icon: ImageVector,
    @androidx.annotation.StringRes val labelRes: Int? = null
)

private fun infoActionHint(ui: InfoLineUi): ActionHint? = when {
    ui.children.isNotEmpty() -> ActionHint(Icons.Default.ExpandMore)
    ui.action == InfoLineAction.Copy -> ActionHint(Icons.Default.ContentCopy, ExtR.string.menu_popup_copy)
    ui.action == InfoLineAction.LaunchActivity ||
        ui.action == InfoLineAction.LaunchService ||
        ui.action == InfoLineAction.LaunchProvider ->
        ActionHint(Icons.Default.PlayArrow, ExtR.string.action_launch_app)
    ui.action == InfoLineAction.OpenSettings ->
        ActionHint(Icons.Default.Settings, ExtR.string.action_settings)
    ui.action == InfoLineAction.ViewXml ->
        ActionHint(Icons.Default.Description, ExtR.string.action_xml_viewer)
    else -> null
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun InfoLineContent(ui: InfoLineUi, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(ui.title, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        if (ui.value.isNotBlank()) {
            Text(
                text = ui.value,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (ui.meta.isNotBlank()) {
            Text(
                text = ui.meta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ui.status?.let { status ->
            val scheme = MaterialTheme.colorScheme
            val (label, color) = when (status) {
                InfoLineStatus.Granted -> "granted" to scheme.tertiary
                InfoLineStatus.Denied -> "denied" to scheme.error
                InfoLineStatus.Exported -> "Exported" to scheme.error
                InfoLineStatus.NotExported -> "Not exported" to scheme.onSurfaceVariant
            }
            Text(label, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvKeyValueBlock(title: String, rows: List<Pair<String, String>>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        rows.forEach { (k, v) ->
            Text(k, style = MaterialTheme.typography.labelMedium)
            Text(
                v,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

private fun dispatchInfoAction(
    context: Context,
    ui: InfoLineUi,
    packageName: String,
    onLaunchService: (String) -> Unit,
    onOpenAppSettings: () -> Unit,
    onViewXml: (String) -> Unit
) {
    when (ui.action) {
        InfoLineAction.Copy -> {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText(ui.title, ui.actionPayload.ifBlank { ui.value }))
            com.walhalla.compose.log.AppLogBus.info(
                context.getString(com.walhalla.extractor.R.string.copied_to_clipboard)
            )
        }
        InfoLineAction.LaunchActivity ->
            LauncherUtils.onLaunchExportedActivity(context, packageName, ui.actionPayload)
        InfoLineAction.LaunchService -> onLaunchService(ui.actionPayload)
        InfoLineAction.LaunchProvider -> {
            val provider = ui.providerLine ?: return
            LauncherUtils.onLaunchProvider(
                context,
                provider,
                object : LauncherUtils.LauCallback {
                    override fun showError(s: String) {
                        com.walhalla.compose.log.AppLogBus.error(s)
                    }
                }
            )
        }
        InfoLineAction.OpenSettings -> onOpenAppSettings()
        InfoLineAction.ViewXml -> onViewXml(ui.actionPayload)
        InfoLineAction.None -> Unit
    }
}
