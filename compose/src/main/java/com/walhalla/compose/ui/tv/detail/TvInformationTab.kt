package com.walhalla.compose.ui.tv.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.Button
import com.walhalla.compose.R
import com.walhalla.compose.detail.AppInsights
import com.walhalla.compose.detail.ReportAction
import com.walhalla.compose.detail.insightFacts
import com.walhalla.compose.ui.tv.components.TvChoice
import com.walhalla.compose.ui.tv.components.TvChoiceDialog
import com.walhalla.compose.ui.tv.components.tvBringIntoViewOnFocus
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
    insights: AppInsights?,
    onLaunchService: (String) -> Unit,
    onOpenAppSettings: () -> Unit,
    onViewXml: (String) -> Unit,
    onReportAction: (ReportAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shelves = remember(appDetails?.infoLines, app.packageName, insights) {
        val overview = insights?.let {
            TvShelf(
                context.getString(R.string.insight_overview),
                0,
                insightFacts(context, it),
                emptyList(),
                emptyList()
            )
        }
        listOfNotNull(overview) + buildShelves(context, app, appDetails)
    }
    val stats = remember(shelves, app.packageName) { buildStats(context, app, shelves) }
    var showReport by remember { mutableStateOf(false) }

    if (showReport) {
        val actions = ReportAction.entries
        TvChoiceDialog(
            title = stringResource(R.string.report_export),
            choices = actions.map { TvChoice(stringResource(it.labelRes), it.icon) },
            onSelect = { onReportAction(actions[it]) },
            onDismiss = { showReport = false }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        item(key = "hero") {
            TvStatHero(
                stats = stats,
                insights = insights,
                onExport = { showReport = true }
            )
        }
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
            TvShelf(
                context.getString(ExtR.string.app_info_basic),
                0,
                fallbackSpecs(context, app),
                emptyList(),
                emptyList()
            )
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
    return TvShelf(title.ifBlank { context.getString(ExtR.string.app_info_basic) }, iconRes, specs, cards, groups)
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

private fun yesNo(context: Context, value: Boolean) =
    context.getString(if (value) R.string.value_yes else R.string.value_no)

private fun fallbackSpecs(context: Context, app: PackageMeta): List<Pair<String, String>> = listOf(
    context.getString(ExtR.string.app_info_package_name) to app.packageName,
    context.getString(R.string.stat_version) to "${app.versionName ?: "?"} (${app.versionCode})",
    context.getString(R.string.stat_size) to prettyFileSize(app.fileSize),
    context.getString(R.string.stat_splits) to yesNo(context, app.hasSplits),
)

private fun buildStats(context: Context, app: PackageMeta, shelves: List<TvShelf>): List<TvStat> {
    val allSpecs = shelves.flatMap { it.specs }
    fun spec(resId: Int): String? {
        val label = runCatching { context.getString(resId) }.getOrNull() ?: return null
        return allSpecs.firstOrNull { it.first == label }?.second
    }
    fun s(id: Int) = context.getString(id)
    return buildList {
        add(TvStat(s(R.string.stat_version), app.versionName ?: app.versionCode.toString()))
        add(TvStat(s(R.string.stat_build), app.versionCode.toString()))
        if (app.fileSize > 0) add(TvStat(s(R.string.stat_size), prettyFileSize(app.fileSize)))
        spec(ExtR.string.app_info_target_sdk)?.let { add(TvStat(s(R.string.stat_target_sdk), it)) }
        spec(ExtR.string.app_info_min_sdk)?.let { add(TvStat(s(R.string.stat_min_sdk), it)) }
        add(
            TvStat(
                s(R.string.stat_type),
                s(if (app.isSystemApp) R.string.type_system else R.string.type_user)
            )
        )
        add(TvStat(s(R.string.stat_splits), yesNo(context, app.hasSplits)))
    }
}

// endregion

// region hero

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun TvStatHero(
    stats: List<TvStat>,
    insights: AppInsights?,
    onExport: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val bringHero = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .bringIntoViewRequester(bringHero)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(colors.primary.copy(alpha = 0.30f), colors.surface.copy(alpha = 0.6f))
                )
            )
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TvStatStrip(stats)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                insights?.let { TvInsightBadges(it) }
            }
            Button(
                onClick = onExport,
                modifier = Modifier.onFocusChanged { state ->
                    if (state.isFocused) {
                        scope.launch { runCatching { bringHero.bringIntoView() } }
                    }
                }
            ) {
                Icon(Icons.Default.IosShare, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.report_export))
            }
        }
        insights?.let { TvStorageAndPerms(it) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowScope.TvInsightBadges(insights: AppInsights) {
    val colors = MaterialTheme.colorScheme
    TvBadge(
        icon = Icons.Default.Tv,
        text = stringResource(if (insights.tvLauncher) R.string.insight_tv_ready else R.string.insight_not_tv),
        color = if (insights.tvLauncher) colors.tertiary else colors.onSurfaceVariant
    )
    TvBadge(
        icon = Icons.Default.TouchApp,
        text = stringResource(
            if (insights.touchscreenRequired) R.string.insight_touch_required else R.string.insight_touch_optional
        ),
        color = if (insights.touchscreenRequired) colors.error else colors.tertiary
    )
    if (insights.abis.isEmpty()) {
        TvBadge(Icons.Default.Memory, stringResource(R.string.insight_abi_none), colors.onSurfaceVariant)
    } else {
        insights.abis.forEach { TvBadge(Icons.Default.Memory, it, colors.primary) }
    }
    TvBadge(
        icon = Icons.Default.Store,
        text = stringResource(insights.installSource.labelRes),
        color = colors.secondary
    )
    if (insights.debuggable) {
        TvBadge(Icons.Default.BugReport, stringResource(R.string.insight_debuggable), colors.error)
    }
    if (insights.splitNames.isNotEmpty()) {
        TvBadge(
            Icons.Default.CallSplit,
            "${stringResource(R.string.stat_splits)}: ${insights.splitNames.size}",
            colors.secondary
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvBadge(icon: ImageVector, text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = color, maxLines = 1)
    }
}

/** Permissions summary + APK / data / cache proportional bar. */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvStorageAndPerms(insights: AppInsights) {
    val colors = MaterialTheme.colorScheme
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.insight_permissions).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.insight_perms_value,
                    insights.permsRequested,
                    insights.permsGranted,
                    insights.permsDangerous
                ),
                style = MaterialTheme.typography.titleSmall,
                color = colors.onSurface
            )
        }
        Column(modifier = Modifier.weight(1.4f)) {
            Text(
                text = stringResource(R.string.insight_storage).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            val apk = insights.apkBytes
            if (apk == null) {
                Text(
                    text = stringResource(R.string.insight_storage_denied),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            } else {
                val parts = listOf(
                    Triple(stringResource(R.string.storage_apk), apk, colors.primary),
                    Triple(stringResource(R.string.storage_data), insights.dataBytes ?: 0L, colors.tertiary),
                    Triple(stringResource(R.string.storage_cache), insights.cacheBytes ?: 0L, colors.secondary),
                )
                val total = parts.sumOf { it.second }.coerceAtLeast(1L)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(colors.surfaceVariant)
                ) {
                    parts.filter { it.second > 0 }.forEach { (_, bytes, color) ->
                        Box(
                            modifier = Modifier
                                .weight(bytes.toFloat() / total)
                                .fillMaxHeight()
                                .background(color)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    parts.forEach { (label, bytes, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(color)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "$label ${prettyFileSize(bytes)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.onSurface
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
private fun TvStatStrip(stats: List<TvStat>) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
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
            .tvBringIntoViewOnFocus()
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
            .tvBringIntoViewOnFocus()
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
    val (labelRes, color) = when (status) {
        InfoLineStatus.Granted -> R.string.status_granted to scheme.tertiary
        InfoLineStatus.Denied -> R.string.status_denied to scheme.error
        InfoLineStatus.Exported -> R.string.status_exported to scheme.primary
        InfoLineStatus.NotExported -> R.string.status_internal to scheme.onSurfaceVariant
    }
    Text(
        text = stringResource(labelRes),
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
