package com.walhalla.compose.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.ResourcesToolForPlugin
import com.walhalla.compose.detail.AppDetailTabs
import com.walhalla.compose.viewmodel.AppDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: PackageMeta,
    onBackClick: () -> Unit,
    onViewManifest: (PackageMeta, xmlFileName: String?) -> Unit = { _, _ -> },
    viewModel: AppDetailViewModel = viewModel()
) {
    val appDetails by viewModel.appDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val needsUsageAccess by viewModel.needsUsageAccess.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(app.packageName) {
        viewModel.loadAppDetails(app)
    }

    LaunchedEffect(needsUsageAccess) {
        if (needsUsageAccess) {
            val result = snackbarHostState.showSnackbar(
                message = "Need special permission package usage stats",
                actionLabel = "SETTINGS",
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.openUsageAccessSettings()
            } else {
                viewModel.clearUsageAccessRequest()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column {
                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onViewManifest(app, null) }) {
                        Icon(Icons.Default.Description, contentDescription = "Manifest")
                    }
                    IconButton(onClick = { viewModel.openInSettings(app) }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val pagerState = rememberPagerState(initialPage = AppDetailTabs.INFO)
            val coroutineScope = rememberCoroutineScope()
            val tabs = AppDetailTabs.titles

            ScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            HorizontalPager(
                count = tabs.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    AppDetailTabs.INFO -> InformationScreen(
                        PaddingValues(0.dp),
                        app,
                        appDetails,
                        onLaunchService = { viewModel.launchExportedService(it) },
                        onOpenAppSettings = { viewModel.openInSettings(app) },
                        onViewXml = { onViewManifest(app, it) }
                    )
                    AppDetailTabs.META -> MetaScreen(app)
                    AppDetailTabs.STRING -> StringScreen(
                        app,
                        ResourcesToolForPlugin.STRING,
                        onViewXml = { onViewManifest(app, it) }
                    )
                    AppDetailTabs.XML -> StringScreen(
                        app,
                        ResourcesToolForPlugin.XML,
                        onViewXml = { onViewManifest(app, it) }
                    )
                    AppDetailTabs.ASSETS -> AssetsScreen(
                        app,
                        onViewXml = { path -> onViewManifest(app, "assets/$path") }
                    )
                }
            }
        }
    }
}
