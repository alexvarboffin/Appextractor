package com.walhalla.compose.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.ActivityLine
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.CertLine
import com.walhalla.appextractor.sdk.DirLine
import com.walhalla.appextractor.sdk.FlagzObject
import com.walhalla.appextractor.sdk.HeaderCollapsedObject
import com.walhalla.appextractor.sdk.HeaderObject
import com.walhalla.appextractor.sdk.InfoApkLine
import com.walhalla.appextractor.sdk.PermissionLine
import com.walhalla.appextractor.sdk.ProviderLine
import com.walhalla.appextractor.sdk.ReceiverLine
import com.walhalla.appextractor.sdk.ServiceLine
import com.walhalla.appextractor.sdk.SimpleLine
import com.walhalla.appextractor.sdk.V2Line
import com.walhalla.appextractor.utils.ApplicationFlags
import com.walhalla.appextractor.utils.LauncherUtils
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.detail.InfoSection
import com.walhalla.compose.detail.groupIntoSections
import com.walhalla.compose.detail.resolvePermissionDescription
import com.walhalla.compose.viewmodel.AppDetails
import com.walhalla.extractor.R as ExtR

@Composable
fun InformationScreen(
    paddingValues: PaddingValues,
    app: PackageMeta,
    appDetails: AppDetails?,
    onLaunchService: (className: String) -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onViewXml: (xmlPath: String) -> Unit = {}
) {
    val sections = remember(appDetails?.infoLines, app.packageName) {
        val lines = appDetails?.infoLines.orEmpty()
        groupIntoSections(lines, app)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (appDetails == null) {
            item {
                ExpandableCard(
                    title = "Basic Information",
                    icon = Icons.Default.Info,
                    initiallyExpanded = true
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Package Name", app.packageName)
                        DetailRow("Version", "${app.versionName} (${app.versionCode})")
                        DetailRow("Size", prettyFileSize(app.fileSize))
                        DetailRow("Has splits", if (app.hasSplits) "Yes" else "No")
                        DetailRow("Pinning", if (app.hasPinning) "Yes" else "No")
                        DetailRow("System App", if (app.isSystemApp) "Yes" else "No")
                        if (app.sourceDir.isNotEmpty()) {
                            DetailRow("Source dir", app.sourceDir)
                        }
                    }
                }
            }
        } else {
            items(sections, key = { it.title + it.items.size }) { section ->
                ExpandableCard(
                    title = section.title.ifBlank { "Details" },
                    iconRes = section.iconRes.takeIf { it != 0 },
                    icon = if (section.iconRes == 0) Icons.Default.Info else null,
                    initiallyExpanded = section.title.contains("Basic", ignoreCase = true)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        section.items.forEach { line ->
                            InfoLineItem(
                                line,
                                app.packageName,
                                onLaunchService,
                                onOpenAppSettings,
                                onViewXml
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoLineItem(
    line: BaseViewModel,
    packageName: String,
    onLaunchService: (String) -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onViewXml: (String) -> Unit = {}
) {
    when (line) {
        is SimpleLine -> {
            val ctx = LocalContext.current
            DetailRow(ctx.getString(line.name), line.value, copyable = true)
        }
        is CertLine -> DetailRow(line.res0.ifBlank { "—" }, line.value, copyable = true)
        is V2Line -> DetailRow(line.key, line.value, copyable = true)
        is DirLine -> DetailRow(line.name, line.value, copyable = true)
        is InfoApkLine -> InfoApkRow(line, onViewXml)
        is PermissionLine -> PermissionRow(line, onOpenAppSettings)
        is FlagzObject -> FlagsRow(line.flags)
        is HeaderCollapsedObject -> CollapsedHeader(
            line, packageName, onLaunchService, onOpenAppSettings, onViewXml
        )
        is ActivityLine -> ActivityRow(line, packageName)
        is ServiceLine -> ServiceRow(line, onLaunchService)
        is ReceiverLine -> ComponentRow(
            label = line.label.ifBlank { line.receiverName.substringAfterLast('.') },
            className = line.receiverName,
            exported = line.exported
        )
        is ProviderLine -> ProviderRow(line, packageName)
        else -> DetailRow(line::class.simpleName ?: "?", line.toString())
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlagsRow(flags: Int) {
    val names = remember(flags) { ApplicationFlags.activeNames(flags) }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Flags (0x${Integer.toHexString(flags)})",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            names.forEach { name ->
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = name.removePrefix("FLAG_"),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
        if (names.isEmpty()) {
            Text(
                text = "No flags set",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CollapsedHeader(
    header: HeaderCollapsedObject,
    packageName: String,
    onLaunchService: (String) -> Unit = {},
    onOpenAppSettings: () -> Unit = {},
    onViewXml: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (header.icon != 0) {
                    Icon(
                        painter = painterResource(header.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = header.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)) {
                header.list.forEach { child ->
                    InfoLineItem(
                        child, packageName, onLaunchService, onOpenAppSettings, onViewXml
                    )
                }
            }
        }
    }
}

private val PermissionGrantedGreen = Color(0xFF2E7D32)
private val PermissionDeniedRed = Color(0xFFC62828)

@Composable
private fun PermissionRow(line: PermissionLine, onOpenAppSettings: () -> Unit = {}) {
    val context = LocalContext.current
    val statusColor = if (line.isGranted) PermissionGrantedGreen else PermissionDeniedRed
    val description = remember(line.res0) {
        resolvePermissionDescription(context, line.res0)
    }
    val level = when (line.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) {
        PermissionInfo.PROTECTION_DANGEROUS -> "dangerous"
        PermissionInfo.PROTECTION_NORMAL -> "normal"
        PermissionInfo.PROTECTION_SIGNATURE -> "signature"
        else -> "level=${line.protectionLevel}"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenAppSettings)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (line.isGranted) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = statusColor,
            modifier = Modifier.padding(top = 2.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = line.res0, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = level,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Text(
            text = if (line.isGranted) "granted" else "denied",
            style = MaterialTheme.typography.labelSmall,
            color = statusColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun ActivityRow(line: ActivityLine, packageName: String) {
    val context = LocalContext.current
    val label = line.label.ifBlank { line.className.substringAfterLast('.') }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = line.className,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (line.exported) "Exported" else "Not exported",
                style = MaterialTheme.typography.labelSmall,
                color = if (line.exported) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        if (line.exported) {
            IconButton(
                onClick = {
                    LauncherUtils.onLaunchExportedActivity(
                        context,
                        packageName,
                        line.className
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Launch",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ServiceRow(line: ServiceLine, onLaunchService: (String) -> Unit) {
    val label = line.label.ifBlank { line.className.substringAfterLast('.') }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = line.className,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (line.exported) "Exported" else "Not exported",
                style = MaterialTheme.typography.labelSmall,
                color = if (line.exported) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        if (line.exported) {
            IconButton(onClick = { onLaunchService(line.className) }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Launch service",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ComponentRow(label: String, className: String, exported: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = className,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (exported) "Exported" else "Not exported",
            style = MaterialTheme.typography.labelSmall,
            color = if (exported) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun InfoApkRow(line: InfoApkLine, onViewXml: (String) -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            DetailRow(line.name, line.value, copyable = true)
        }
        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(com.walhalla.extractor.R.string.action_xml_viewer)) },
                    onClick = {
                        showMenu = false
                        onViewXml(line.value)
                    }
                )
            }
        }
    }
}

@Composable
private fun ProviderRow(line: ProviderLine, packageName: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = line.label.ifBlank { line.className.substringAfterLast('.') },
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = line.className,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (line.authority.isNotBlank()) {
                DetailRow("Authority", line.authority, copyable = true)
            }
            Text(
                text = buildString {
                    append(if (line.exported) "Exported" else "Not exported")
                    append(" · ")
                    append(if (line.enabled) "Enabled" else "Disabled")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (line.exported && line.authority.isNotBlank()) {
            IconButton(
                onClick = {
                    LauncherUtils.onLaunchProvider(
                        context,
                        line,
                        object : LauncherUtils.LauCallback {
                            override fun showError(s: String) {
                                Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Query provider",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ExpandableCard(
    title: String,
    icon: ImageVector? = null,
    iconRes: Int? = null,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    when {
                        iconRes != null && iconRes != 0 -> Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        icon != null -> Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                content()
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    copyable: Boolean = false
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (copyable && value.isNotBlank()) {
                    Modifier.clickable {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText(label, value))
                        Toast.makeText(context, ExtR.string.copied_to_clipboard, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else Modifier
            )
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 6,
            overflow = TextOverflow.Ellipsis
        )
    }
}
