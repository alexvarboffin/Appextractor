package com.walhalla.compose.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.viewmodel.AppDetailViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.walhalla.appextractor.sdk.ResourcesToolForPlugin
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

import com.walhalla.compose.viewmodel.AppDetails


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: PackageMeta,
    onBackClick: () -> Unit,
    viewModel: AppDetailViewModel = viewModel()
) {
    val appDetails by viewModel.appDetails.collectAsState()

    LaunchedEffect(app) {
        viewModel.loadAppDetails(app)
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openInSettings(app) }) {
                        Icon(Icons.Default.Settings, contentDescription = "App Settings")
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

            TabRow(selectedTabIndex = pagerState.currentPage) {
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

            HorizontalPager(
                count = tabs.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> InformationScreen(paddingValues, app, appDetails)
                    1 -> MetaScreen(app)
                    2 -> StringScreen(app, ResourcesToolForPlugin.STRING)
                    3 -> StringScreen(app, ResourcesToolForPlugin.XML)
                    4 -> AssetsScreen(app)
                }
            }
        }
    }
}



