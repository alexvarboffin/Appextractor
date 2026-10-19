package com.walhalla.compose.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PermissionInfo
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.viewmodel.AppDetails
import com.walhalla.extractor.R as ExtR

private data class InfoSection(
    val title: String,
    val iconRes: Int,
    val items: List<BaseViewModel>
)

@Composable
fun InformationScreen(
    paddingValues: PaddingValues,
    app: PackageMeta,
    appDetails: AppDetails?
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
                            InfoLineItem(line)
                        }
                    }
                }
            }
        }
    }
}

private fun groupIntoSections(
    lines: List<BaseViewModel>,
    app: PackageMeta
): List<InfoSection> {
    val sections = mutableListOf<InfoSection>()
    var title = ""
    var icon = 0
    var bucket = mutableListOf<BaseViewModel>()

    fun flush() {
        if (title.isNotEmpty() || bucket.isNotEmpty()) {
            sections.add(InfoSection(title, icon, bucket.toList()))
        }
        bucket = mutableListOf()
    }

    // PackageMeta-only fields that F0Presenter does not always expose
    val extras = mutableListOf<BaseViewModel>()
    if (app.fileSize > 0) {
        extras.add(V2Line("Size", prettyFileSize(app.fileSize)))
    }
    extras.add(V2Line("Has splits", if (app.hasSplits) "Yes" else "No"))
    extras.add(V2Line("Certificate pinning", if (app.hasPinning) "Yes" else "No"))

    for (line in lines) {
        when (line) {
            is HeaderObject -> {
                flush()
                title = line.title
                icon = line.icon
                if (title.contains("Basic", ignoreCase = true)) {
                    bucket.addAll(extras)
                }
            }
            else -> bucket.add(line)
        }
    }
    flush()

    if (sections.isEmpty() && extras.isNotEmpty()) {
        sections.add(InfoSection("Basic Information", 0, extras))
    }
    return sections
}

@Composable
private fun InfoLineItem(line: BaseViewModel) {
    when (line) {
        is SimpleLine -> {
            val ctx = LocalContext.current
            DetailRow(ctx.getString(line.name), line.value, copyable = true)
        }
        is CertLine -> DetailRow(line.res0.ifBlank { "—" }, line.value, copyable = true)
        is V2Line -> DetailRow(line.key, line.value, copyable = true)
        is DirLine -> DetailRow(line.name, line.value, copyable = true)
        is InfoApkLine -> DetailRow(line.name, line.value, copyable = true)
        is PermissionLine -> PermissionRow(line)
        is FlagzObject -> FlagsRow(line.flags)
        is HeaderCollapsedObject -> CollapsedHeader(line)
        is ActivityLine -> ComponentRow(
            label = line.label.ifBlank { line.className.substringAfterLast('.') },
            className = line.className,
            exported = line.exported
        )
        is ServiceLine -> ComponentRow(
            label = line.label.ifBlank { line.className.substringAfterLast('.') },
            className = line.className,
            exported = line.exported
        )
        is ReceiverLine -> ComponentRow(
            label = line.label.ifBlank { line.receiverName.substringAfterLast('.') },
            className = line.receiverName,
            exported = line.exported
        )
        is ProviderLine -> ProviderRow(line)
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
private fun CollapsedHeader(header: HeaderCollapsedObject) {
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
                    InfoLineItem(child)
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(line: PermissionLine) {
    val level = when (line.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) {
        PermissionInfo.PROTECTION_DANGEROUS -> "dangerous"
        PermissionInfo.PROTECTION_NORMAL -> "normal"
        PermissionInfo.PROTECTION_SIGNATURE -> "signature"
        else -> "level=${line.protectionLevel}"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (line.isGranted) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (line.isGranted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = line.res0, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = level,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = if (line.isGranted) "granted" else "denied",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
private fun ProviderRow(line: ProviderLine) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
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
