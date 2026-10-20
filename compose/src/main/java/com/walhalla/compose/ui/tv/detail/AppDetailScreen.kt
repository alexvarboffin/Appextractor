package com.walhalla.compose.ui.tv.detail

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.ResourcesToolForPlugin
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.detail.AppDetailTabs
import com.walhalla.compose.ui.tv.components.TvFocusTabRow
import com.walhalla.compose.viewmodel.AppDetailViewModel
import com.walhalla.compose.viewmodel.ExtractorViewModel
import com.walhalla.extractor.R as ExtR

/**
 * TV app detail: phone overflow actions + full INFO/META/STRING/XML/ASSETS parity.
 * Shared VMs / [AppDetailTabs] / info+meta mappers — UI is tv.material3 only.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppDetailScreen(
    packageName: String,
    initialApp: PackageMeta?,
    onBack: () -> Unit,
    onViewManifest: (PackageMeta, xmlFileName: String?) -> Unit = { _, _ -> }
) {
    val activity = LocalContext.current as ComponentActivity
    val extractorViewModel: ExtractorViewModel = viewModel(viewModelStoreOwner = activity)
    val detailViewModel: AppDetailViewModel = viewModel()
    val app = remember(packageName, initialApp) {
        initialApp ?: extractorViewModel.getAppByPackageName(packageName)
    }
    val appDetails by detailViewModel.appDetails.collectAsState()
    val isLoading by detailViewModel.isLoading.collectAsState()
    val needsUsageAccess by detailViewModel.needsUsageAccess.collectAsState()
    val firstAction = remember { FocusRequester() }
    val tabRowFallback = remember { FocusRequester() }
    var selectedTab by remember { mutableIntStateOf(AppDetailTabs.INFO) }

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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
        LaunchedEffect(Unit) { runCatching { firstAction.requestFocus() } }
        return
    }

    val actions = remember(app.packageName) {
        listOf(
            TvAction(ExtR.string.action_extract) { extractorViewModel.extractApkRequest(app) },
            TvAction(ExtR.string.action_share_app) { extractorViewModel.shareApk(app) },
            TvAction(ExtR.string.open_on_google_play) { extractorViewModel.openPlayStore(app) },
            TvAction(ExtR.string.action_launch_app) { extractorViewModel.launchApp(app) },
            TvAction(ExtR.string.action_uninstall_app) { extractorViewModel.uninstallApp(app) },
            TvAction(ExtR.string.action_copy_package_name) { extractorViewModel.copyPackageName(app) },
            TvAction(ExtR.string.action_save_icon) { extractorViewModel.saveIcon(app) },
            TvAction(ExtR.string.action_export_icon) { extractorViewModel.shareIcon(app) },
            TvAction(ExtR.string.action_app_manifest) { onViewManifest(app, null) },
            TvAction(ExtR.string.action_app_info) { detailViewModel.openInSettings(app) },
        )
    }

    LaunchedEffect(Unit) {
        runCatching { firstAction.requestFocus() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (app.icon == null) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                AsyncImage(
                    model = app.icon,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
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

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(actions.size) { index ->
                val action = actions[index]
                Button(
                    onClick = action.onClick,
                    modifier = if (index == 0) Modifier.focusRequester(firstAction) else Modifier
                ) {
                    Text(stringResource(action.labelRes))
                }
            }
        }

        if (needsUsageAccess) {
            Button(
                onClick = { detailViewModel.openUsageAccessSettings() },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Usage access · SETTINGS")
            }
        }

        if (isLoading) {
            Text(
                stringResource(ExtR.string.dlg_loading_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        TvFocusTabRow(
            tabs = AppDetailTabs.titles,
            selectedIndex = selectedTab,
            onSelectedIndexChange = { selectedTab = it },
            tabRowFallback = tabRowFallback
        )

        when (selectedTab) {
            AppDetailTabs.INFO -> TvInformationTab(
                app = app,
                appDetails = appDetails,
                onLaunchService = { detailViewModel.launchExportedService(it) },
                onOpenAppSettings = { detailViewModel.openInSettings(app) },
                onViewXml = { onViewManifest(app, it) },
                modifier = Modifier.weight(1f)
            )
            AppDetailTabs.META -> TvMetaTab(app, Modifier.weight(1f))
            AppDetailTabs.STRING -> TvStringTab(
                app = app,
                resourceType = ResourcesToolForPlugin.STRING,
                onViewXml = { onViewManifest(app, it) },
                modifier = Modifier.weight(1f)
            )
            AppDetailTabs.XML -> TvStringTab(
                app = app,
                resourceType = ResourcesToolForPlugin.XML,
                onViewXml = { onViewManifest(app, it) },
                modifier = Modifier.weight(1f)
            )
            AppDetailTabs.ASSETS -> TvAssetsTab(
                app = app,
                onViewXml = { path -> onViewManifest(app, "assets/$path") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class TvAction(@StringRes val labelRes: Int, val onClick: () -> Unit)
