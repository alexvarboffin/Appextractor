package com.walhalla.compose.ui.tv.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.LauncherUtils
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.detail.InfoLineAction
import com.walhalla.compose.detail.InfoLineStatus
import com.walhalla.compose.detail.InfoLineUi
import com.walhalla.compose.detail.InfoSection
import com.walhalla.compose.detail.groupIntoSections
import com.walhalla.compose.detail.mapInfoLine
import com.walhalla.compose.log.AppLogBus
import com.walhalla.compose.ui.tv.components.TvActionHintIcon
import com.walhalla.compose.viewmodel.AppDetails
import com.walhalla.extractor.R as ExtR

/**
 * TV INFO = catalog page, not a row per field:
 * hero stat strip → per section one spec sheet (single focus, OK copies the section)
 * + a card rail only for interactive components (launch / permissions / XML / status).
 * Data stays shared: [groupIntoSections] + [mapInfoLine].
 */
private data class TvShelf(
    val title: String,
    val iconRes: Int,
    val specs: List<Pair<String, String>>,
    val cards: List<InfoLineUi>,
    val groups: List<TvShelfGroup>,
)

private data class TvShelfGroup(
    val title: String,
    val specs: List<Pair<String, String>>,
    val cards: List<InfoLineUi>,
)

private data class TvStat(val label: String, val value: String)

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
    val shelves = remember(appDetails?.infoLines, app.packageName) {
        buildShelves(context, app, appDetails)
    }
    val stats = remember(shelves, app.packageName) { buildStats(context, app, shelves) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item(key = "hero") { TvStatHero(stats) }
        itemsIndexed(shelves, key = { index, shelf -> "$index-${shelf.title}" }) { _, shelf ->
            TvShelfBlock(
                shelf = shelf,
                packageName = app.packageName,
                onLaunchService = onLaunchService,
                onOpenAppSettings = onOpenAppSettings,
                onViewXml = onViewXml,
            )
        }
    }
}

// region data

private fun buildShelves(
    context: Context,
    app: PackageMeta,
    appDetails: AppDetails?,
): List<TvShelf> {
    if (appDetails == null) {
        return listOf(
            TvShelf("Basic Information", 0, fallbackSpecs(app), emptyList(), emptyList())
        )
    }
    return groupIntoSections(appDetails.infoLines, app).map { sec -> sec.toShelf(context, app) }
        .filter { it.specs.isNotEmpty() || it.cards.isNotEmpty() || it.groups.isNotEmpty() }
}

private fun InfoSection.toShelf(context: Context, app: PackageMeta): TvShelf {
    val specs = mutableListOf<Pair<String, String>>()
    val cards = mutableListOf<InfoLineUi>()
    val groups = mutableListOf<TvShelfGroup>()
    for (line in items) {
        val ui = mapInfoLine(context, line, app.packageName) ?: continue
        partition(ui, specs, cards, groups)
    }
    return TvShelf(title.ifBlank { "Details" }, iconRes, specs, cards, groups)
}

/** Plain values (copy-only) → spec sheet; anything with a status or a real action → card. */
private fun InfoLineUi.isCard(): Boolean =
    status != null || (action != InfoLineAction.None && action != InfoLineAction.Copy)

private fun partition(
    ui: InfoLineUi,
    specs: MutableList<Pair<String, String>>,
    cards: MutableList<InfoLineUi>,
    groups: MutableList<TvShelfGroup>,
) {
    if (ui.children.isNotEmpty()) {
        val childSpecs = mutableListOf<Pair<String, String>>()
        val childCards = mutableListOf<InfoLineUi>()
        ui.children.forEach { partition(it, childSpecs, childCards, groups) }
        groups.add(TvShelfGroup(ui.title, childSpecs, childCards))
        return
    }
    if (ui.isCard()) cards.add(ui) else specs.add(ui.title to specValue(ui))
}

private fun specValue(ui: InfoLineUi): String = buildString {
    append(ui.value)
    if (ui.meta.isNotBlank()) {
        if (isNotEmpty()) append(" · ")
        append(ui.meta)
    }
}.ifBlank { "—" }

private fun fallbackSpecs(app: PackageMeta): List<Pair<String, String>> = listOf(
    "Package" to app.packageName,
    "Version" to "${app.versionName ?: "?"} (${app.versionCode})",
    "Size" to prettyFileSize(app.fileSize),
    "Splits" to if (app.hasSplits) "Yes" else "No",
    "Pinning" to if (app.hasPinning) "Yes" else "No",
)

private fun buildStats(context: Context, app: PackageMeta, shelves: List<TvShelf>): List<TvStat> {
    val allSpecs = shelves.flatMap { it.specs }
    fun spec(resId: Int): String? {
        val label = runCatching { context.getString(resId) }.getOrNull() ?: return null
        return allSpecs.firstOrNull { it.first == label }?.second
    }
    return buildList {
        add(TvStat("Version", app.versionName ?: app.versionCode.toString()))
        add(TvStat("Build", app.versionCode.toString()))
        if (app.fileSize > 0) add(TvStat("Size", prettyFileSize(app.fileSize)))
        spec(ExtR.string.app_info_target_sdk)?.let { add(TvStat("Target SDK", it)) }
        spec(ExtR.string.app_info_min_sdk)?.let { add(TvStat("Min SDK", it)) }
        add(TvStat("Type", if (app.isSystemApp) "System" else "User"))
        add(TvStat("Splits", if (app.hasSplits) "Yes" else "No"))
    }
}

// endregion

// region hero

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvStatHero(stats: List<TvStat>) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(colors.primary.copy(alpha = 0.30f), colors.surface.copy(alpha = 0.6f))
                )
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface.copy(alpha = 0.55f))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stat.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// endregion

// region shelf

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvShelfBlock(
    shelf: TvShelf,
    packageName: String,
    onLaunchService: (String) -> Unit,
    onOpenAppSettings: () -> Unit,
    onViewXml: (String) -> Unit,
) {
    val total = shelf.specs.size + shelf.cards.size +
        shelf.groups.sumOf { it.specs.size + it.cards.size }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TvShelfHeader(title = shelf.title, iconRes = shelf.iconRes, count = total)
        if (shelf.specs.isNotEmpty()) {
            TvSpecSheet(title = shelf.title, specs = shelf.specs)
        }
        if (shelf.cards.isNotEmpty()) {
            TvCardRail(shelf.cards, packageName, onLaunchService, onOpenAppSettings, onViewXml)
        }
        shelf.groups.forEach { group ->
            Text(
                text = group.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
            if (group.specs.isNotEmpty()) TvSpecSheet(title = group.title, specs = group.specs)
            if (group.cards.isNotEmpty()) {
                TvCardRail(group.cards, packageName, onLaunchService, onOpenAppSettings, onViewXml)
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvShelfHeader(title: String, iconRes: Int, count: Int) {
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(start = 4.dp)
    ) {
        if (iconRes != 0) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurface
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(colors.primary)
                .padding(horizontal = 10.dp, vertical = 2.dp)
        )
    }
}

/** Whole section of plain values = one focus target; OK copies all "label: value" lines. */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvSpecSheet(title: String, specs: List<Pair<String, String>>) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    var focused by remember { mutableStateOf(false) }
    Surface(
        onClick = {
            copyToClipboard(context, title, specs.joinToString("\n") { "${it.first}: ${it.second}" })
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(16.dp)),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.01f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = colors.surfaceVariant.copy(alpha = 0.45f),
            focusedContainerColor = colors.surfaceVariant
        )
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            TvSpecGrid(specs = specs, columns = 3)
            TvActionHintIcon(
                icon = Icons.Default.ContentCopy,
                focused = focused,
                contentDescription = stringResource(ExtR.string.menu_popup_copy),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
private fun TvSpecGrid(specs: List<Pair<String, String>>, columns: Int) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        specs.chunked(columns).forEach { chunk ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                chunk.forEach { (label, value) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                    ) {
                        Text(
                            text = label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurface,
                            fontFamily = if (value.looksTechnical()) FontFamily.Monospace else null,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                repeat(columns - chunk.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

private fun String.looksTechnical(): Boolean =
    startsWith("/") || contains("://") || (length > 24 && none { it == ' ' })

// endregion

// region cards

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvCardRail(
    cards: List<InfoLineUi>,
    packageName: String,
    onLaunchService: (String) -> Unit,
    onOpenAppSettings: () -> Unit,
    onViewXml: (String) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        itemsIndexed(cards, key = { index, ui -> "$index-${ui.title}-${ui.actionPayload}" }) { _, ui ->
            TvComponentCard(ui, packageName, onLaunchService, onOpenAppSettings, onViewXml)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvComponentCard(
    ui: InfoLineUi,
    packageName: String,
    onLaunchService: (String) -> Unit,
    onOpenAppSettings: () -> Unit,
    onViewXml: (String) -> Unit,
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    var focused by remember { mutableStateOf(false) }
    val icon = cardIcon(ui)

    Surface(
        onClick = { dispatchInfoAction(context, ui, packageName, onLaunchService, onOpenAppSettings, onViewXml) },
        modifier = Modifier
            .width(300.dp)
            .height(132.dp)
            .onFocusChanged { focused = it.isFocused },
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(16.dp)),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.08f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = colors.surface,
            focusedContainerColor = colors.primary.copy(alpha = 0.30f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (focused) colors.primary else colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = ui.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = ui.value,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                ui.status?.let { TvStatusPill(it) }
                if (ui.meta.isNotBlank()) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = ui.meta,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvStatusPill(status: InfoLineStatus) {
    val scheme = MaterialTheme.colorScheme
    val (label, color) = when (status) {
        InfoLineStatus.Granted -> "granted" to scheme.tertiary
        InfoLineStatus.Denied -> "denied" to scheme.error
        InfoLineStatus.Exported -> "exported" to scheme.primary
        InfoLineStatus.NotExported -> "internal" to scheme.onSurfaceVariant
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

private fun cardIcon(ui: InfoLineUi): ImageVector = when (ui.action) {
    InfoLineAction.LaunchActivity,
    InfoLineAction.LaunchService,
    InfoLineAction.LaunchProvider -> Icons.Default.PlayArrow
    InfoLineAction.OpenSettings -> Icons.Default.Settings
    InfoLineAction.ViewXml -> Icons.Default.Description
    InfoLineAction.Copy -> Icons.Default.ContentCopy
    InfoLineAction.None -> Icons.Default.Extension
}

// endregion

private fun copyToClipboard(context: Context, label: String, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText(label, text))
    AppLogBus.info(context.getString(ExtR.string.copied_to_clipboard))
}

private fun dispatchInfoAction(
    context: Context,
    ui: InfoLineUi,
    packageName: String,
    onLaunchService: (String) -> Unit,
    onOpenAppSettings: () -> Unit,
    onViewXml: (String) -> Unit,
) {
    when (ui.action) {
        InfoLineAction.Copy -> copyToClipboard(context, ui.title, ui.actionPayload.ifBlank { ui.value })
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
                        AppLogBus.error(s)
                    }
                }
            )
        }
        InfoLineAction.OpenSettings -> onOpenAppSettings()
        InfoLineAction.ViewXml -> onViewXml(ui.actionPayload)
        InfoLineAction.None -> Unit
    }
}
