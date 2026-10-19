package com.walhalla.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.extractor.R as ExtR
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

private val IconSize = 40.dp
private val ContentStart = 12.dp
private val IconTextGap = 12.dp
private val FeaturesStart = ContentStart + IconSize + IconTextGap

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppListItem(
    app: PackageMeta,
    isSelected: Boolean = false,
    highlightQuery: String = "",
    onSelect: () -> Unit = {},
    onExtractClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onOpenPlayStoreClick: () -> Unit = {},
    onLaunchClick: () -> Unit = {},
    onUninstallClick: () -> Unit = {},
    onCopyPackageNameClick: () -> Unit = {},
    onManifestClick: () -> Unit = {},
    onSaveIconClick: () -> Unit = {},
    onShareIconClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val menuScope = rememberCoroutineScope()

    /** Close dropdown first, then run action so navigation isn't blocked by open menu. */
    fun runAfterMenuClose(action: () -> Unit) {
        showMenu = false
        menuScope.launch {
            yield()
            action()
        }
    }

    val dateTimeFormat = remember {
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    }
    val installMs = remember(app.firstInstallTime, app.installTime) {
        app.firstInstallTime.takeIf { it > 0 } ?: app.installTime
    }
    val updateMs = remember(app.lastUpdateTime, app.updateTime) {
        app.lastUpdateTime.takeIf { it > 0 } ?: app.updateTime
    }
    val versionLabel = remember(app.versionName, app.versionCode) {
        "v${app.versionName ?: "?"} (${app.versionCode})"
    }

    val features = remember(app.hasSplits, app.isSystemApp, app.hasPinning) {
        buildList {
            if (app.hasSplits) add(FeatureKind.Split)
            if (app.isSystemApp) add(FeatureKind.System)
            if (app.hasPinning) add(FeatureKind.SslPinning)
        }
    }

    val highlightColor = MaterialTheme.colorScheme.primary
    val query = highlightQuery.trim()
    val highlightedLabel = remember(app.label, query, highlightColor) {
        highlightAnnotated(app.label, query, highlightColor)
    }
    val highlightedPackage = remember(app.packageName, query, highlightColor) {
        highlightAnnotated(app.packageName, query, highlightColor)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, onClick = onSelect),
        // Distinct from selection bar (secondaryContainer) so the first row doesn't blend
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = ContentStart, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (app.icon == null) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        modifier = Modifier.size(IconSize)
                    )
                } else {
                    AsyncImage(
                        model = app.icon,
                        contentDescription = null,
                        modifier = Modifier.size(IconSize)
                    )
                }

                Spacer(modifier = Modifier.width(IconTextGap))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = highlightedLabel,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = highlightedPackage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = versionLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Legacy parity: first install + last update | size (Compose density)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            if (installMs > 0) {
                                Text(
                                    text = stringResource(
                                        ExtR.string.backup_app_feature_install_date,
                                        dateTimeFormat.format(Date(installMs))
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (updateMs > 0) {
                                Text(
                                    text = stringResource(
                                        ExtR.string.backup_app_feature_update_date,
                                        dateTimeFormat.format(Date(updateMs))
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (app.fileSize > 0) {
                            Text(
                                text = prettyFileSize(app.fileSize),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_extract)) },
                            leadingIcon = {
                                Icon(Icons.Default.SaveAlt, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onExtractClick) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.open_on_google_play)) },
                            leadingIcon = {
                                Icon(Icons.Default.Shop, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onOpenPlayStoreClick) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_launch_app)) },
                            leadingIcon = {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onLaunchClick) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_uninstall_app)) },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onUninstallClick) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_copy_package_name)) },
                            leadingIcon = {
                                Icon(Icons.Default.ContentCopy, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onCopyPackageNameClick) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_app_info)) },
                            leadingIcon = {
                                Icon(Icons.Default.Info, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onInfoClick) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_app_manifest)) },
                            leadingIcon = {
                                Icon(Icons.Default.Description, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onManifestClick) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_save_icon)) },
                            leadingIcon = {
                                Icon(Icons.Default.Download, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onSaveIconClick) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_export_icon)) },
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null)
                            },
                            onClick = { runAfterMenuClose(onShareIconClick) }
                        )
                    }
                }
            }

            if (features.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = FeaturesStart, end = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    features.forEach { kind ->
                        FeatureBadge(kind = kind)
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

/**
 * Case-insensitive highlight of all [query] occurrences (legacy AppVh.setAndHighlight).
 * Cheap: only visible LazyColumn items + remember(text, query).
 */
private fun highlightAnnotated(
    text: String,
    query: String,
    highlightColor: Color
): AnnotatedString {
    if (query.isEmpty()) {
        return AnnotatedString(text)
    }
    val lowerText = text.lowercase(Locale.getDefault())
    val lowerQuery = query.lowercase(Locale.getDefault())
    if (!lowerText.contains(lowerQuery)) {
        return AnnotatedString(text)
    }
    return buildAnnotatedString {
        var start = 0
        while (start < text.length) {
            val index = lowerText.indexOf(lowerQuery, start)
            if (index < 0) {
                append(text.substring(start))
                break
            }
            if (index > start) {
                append(text.substring(start, index))
            }
            withStyle(
                SpanStyle(
                    color = highlightColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(text.substring(index, index + lowerQuery.length))
            }
            start = index + lowerQuery.length
        }
    }
}

private enum class FeatureKind {
    Split,
    System,
    SslPinning
}

@Composable
private fun FeatureBadge(kind: FeatureKind) {
    val label = when (kind) {
        FeatureKind.Split -> stringResource(ExtR.string.backup_app_feature_split)
        FeatureKind.System -> stringResource(ExtR.string.backup_app_feature_system_app)
        FeatureKind.SslPinning -> stringResource(ExtR.string.backup_app_feature_ssl_pinning)
    }
    val containerColor = when (kind) {
        FeatureKind.Split -> MaterialTheme.colorScheme.tertiaryContainer
        FeatureKind.System -> MaterialTheme.colorScheme.primaryContainer
        FeatureKind.SslPinning -> MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = when (kind) {
        FeatureKind.Split -> MaterialTheme.colorScheme.onTertiaryContainer
        FeatureKind.System -> MaterialTheme.colorScheme.onPrimaryContainer
        FeatureKind.SslPinning -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1
        )
    }
}
