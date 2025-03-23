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
    val appDetails : AppDetails? by viewModel.appDetails.collectAsState()

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
//!!! androidx.compose.foundation.pager.HorizontalPager !!!!
//!!!         InformationScreen(paddingValues, app, appDetails)
    }
}



@Composable
private fun ManifestTab(app: PackageMeta) {
    ManifestScreen(
        app = app,
        onBackClick = { /* Ignore */ }
    )
}

@Composable
private fun ResourcesTab(app: PackageMeta) {
    // TODO: Implement Resources tab
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Resources coming soon...")
    }
} 