package com.walhalla.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.compose.components.LogItem
import com.walhalla.compose.viewmodel.LogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    viewModel: LogViewModel = viewModel()
) {
    val logs by viewModel.logs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs") },
                actions = {
                    // Refresh button
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    // Clear logs button
                    IconButton(onClick = { viewModel.clearLogs() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear logs"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(logs) { log ->
                        LogItem(
                            log = log,
                            onDeleteClick = { filePath ->
                                viewModel.deleteFile(filePath)
                            }
                        )
                    }
                }
            }
        }
    }
} 