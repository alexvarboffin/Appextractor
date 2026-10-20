package com.walhalla.compose.ui.tv.detail

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.HeaderObject
import com.walhalla.appextractor.sdk.V2Line
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.viewmodel.AppDetailViewModel
import com.walhalla.compose.viewmodel.ExtractorViewModel

/**
 * TV app detail: list overflow actions live here as a single-focus action row
 * (no nested menus on the extractor list — D-pad).
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppDetailScreen(
    packageName: String,
    initialApp: PackageMeta?,
    onBack: () -> Unit
) {
    val activity = LocalContext.current as ComponentActivity
    val extractorViewModel: ExtractorViewModel = viewModel(viewModelStoreOwner = activity)
    val detailViewModel: AppDetailViewModel = viewModel()
    val app = remember(packageName, initialApp) {
        initialApp
            ?: extractorViewModel.getAppByPackageName(packageName)
    }
    val appDetails by detailViewModel.appDetails.collectAsState()
    val isLoading by detailViewModel.isLoading.collectAsState()
    val firstAction = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        extractorViewModel.activityProvider = { activity }
    }

    LaunchedEffect(app?.packageName) {
        app?.let { detailViewModel.loadAppDetails(it) }
    }

    BackHandler(onBack = onBack)

    if (app == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("App not found: $packageName", color = MaterialTheme.colorScheme.onSurface)
            Button(onClick = onBack, modifier = Modifier.focusRequester(firstAction)) {
                Text("Back")
            }
        }
        LaunchedEffect(Unit) { runCatching { firstAction.requestFocus() } }
        return
    }

    val actions = remember(app.packageName) {
        listOf(
            TvAction("Extract") { extractorViewModel.extractApkRequest(app) },
            TvAction("Share APK") { extractorViewModel.shareApk(app) },
            TvAction("Play Store") { extractorViewModel.openPlayStore(app) },
            TvAction("Launch") { extractorViewModel.launchApp(app) },
            TvAction("Uninstall") { extractorViewModel.uninstallApp(app) },
            TvAction("Copy package") { extractorViewModel.copyPackageName(app) },
            TvAction("Save icon") { extractorViewModel.saveIcon(app) },
            TvAction("Share icon") { extractorViewModel.shareIcon(app) },
            TvAction("Back", onBack)
        )
    }

    LaunchedEffect(Unit) {
        runCatching { firstAction.requestFocus() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (app.icon != null) {
                AsyncImage(
                    model = app.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(app.label, style = MaterialTheme.typography.headlineSmall)
                Text(app.packageName, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "v${app.versionName ?: "?"} (${app.versionCode})" +
                        if (app.fileSize > 0) " · ${prettyFileSize(app.fileSize)}" else "",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Actions: one focusable control each — no PopupMenu / DropdownMenu on TV.
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(actions.size) { index ->
                val action = actions[index]
                Button(
                    onClick = action.onClick,
                    modifier = if (index == 0) Modifier.focusRequester(firstAction) else Modifier
                ) {
                    Text(action.label)
                }
            }
        }

        Text(
            text = if (isLoading) "Loading details…" else "Info",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val lines = appDetails?.infoLines.orEmpty().take(80)
            if (lines.isEmpty() && !isLoading) {
                item {
                    Text(
                        "No detail rows",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(lines.size) { i ->
                Text(
                    text = formatInfoLine(lines[i]),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatInfoLine(line: BaseViewModel): String = when (line) {
    is HeaderObject -> "— ${line.title}"
    is V2Line -> "${line.key}: ${line.value}"
    else -> line.javaClass.simpleName
}

private data class TvAction(val label: String, val onClick: () -> Unit)
