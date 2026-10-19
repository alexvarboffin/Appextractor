package com.walhalla.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.ResourcesToolForPlugin
import com.walhalla.compose.viewmodel.AppDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: PackageMeta,
    onBackClick: () -> Unit,
    viewModel: AppDetailViewModel = viewModel()
) {
    val appDetails by viewModel.appDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Screen opens immediately; details load in background
    LaunchedEffect(app.packageName) {
        viewModel.loadAppDetails(app)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
            val pagerState = rememberPagerState(initialPage = 0)
            val coroutineScope = rememberCoroutineScope()
            val tabs = listOf("INFO", "META", "STRING", "XML", "ASSETS")

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
                    // Basic info from PackageMeta is immediate; permissions/components fill in
                    // Scaffold already applied paddingValues to the parent Column
                    0 -> InformationScreen(PaddingValues(0.dp), app, appDetails)
                    1 -> MetaScreen(app)
                    2 -> StringScreen(app, ResourcesToolForPlugin.STRING)
                    3 -> StringScreen(app, ResourcesToolForPlugin.XML)
                    4 -> AssetsScreen(app)
                }
            }
        }
    }
}
