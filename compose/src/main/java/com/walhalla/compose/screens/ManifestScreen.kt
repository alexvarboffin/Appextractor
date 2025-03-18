package com.walhalla.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.compose.viewmodel.ManifestViewModel
import com.walhalla.appextractor.model.PackageMeta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManifestScreen(
    app: PackageMeta,
    onBackClick: () -> Unit,
    viewModel: ManifestViewModel = viewModel()
) {
    val manifestContent by viewModel.manifestContent.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(app) {
        viewModel.loadManifest(app)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${app.label} Manifest") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    SelectionContainer {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            item {
                                Text(
                                    text = manifestContent ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 