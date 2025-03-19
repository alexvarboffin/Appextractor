package com.walhalla.compose.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.compose.components.AppListItem
import com.walhalla.compose.model.AppModel
import com.walhalla.compose.viewmodel.ExtractorViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.Divider
import com.walhalla.appextractor.model.PackageMeta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorScreen(
    viewModel: ExtractorViewModel = viewModel(),
    onNavigateToAppDetail: (String) -> Unit,
    viewManifest: (app: PackageMeta) -> Unit
) {
    val context = LocalContext.current
    val apps by viewModel.apps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()
    val extractionProgress by viewModel.extractionProgress.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchActive by remember { mutableStateOf(false) }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.all { it }
        println("DEBUG: Permissions result: $permissionsMap")
        if (allGranted) {
            println("DEBUG: All permissions granted")
        } else {
            println("DEBUG: Some permissions denied")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!isSearchActive) {
                        Text(stringResource(com.walhalla.extractor.R.string.app_name))
                    }
                },
                actions = {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        onSearch = { isSearchActive = false },
                        active = isSearchActive,
                        onActiveChange = { isSearchActive = it },
                        placeholder = { Text(stringResource(com.walhalla.extractor.R.string.search_hint)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.animateContentSize()
                    ) {
                        // Search suggestions will go here
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
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Selection header
                val currentSelectedSize = selectedApps.size
                println("DEBUG: Current selected size: $currentSelectedSize")
                if (selectedApps.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(
                                    com.walhalla.extractor.R.string.action_selected,
                                    currentSelectedSize
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { viewModel.clearSelection() }
                                ) {
                                    Text(
                                        text = "Deselect",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.selectExtractedApps() }
                                ) {
                                    Text(
                                        text = "Select extracted",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // Apps list
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(apps) { app ->
                                AppListItem(
                                    app = app,
                                    isSelected = app in selectedApps,
                                    onSelect = {
                                        println("DEBUG: Selection clicked for ${app.label}")
                                        viewModel.toggleAppSelection(app)
                                    },
                                    onExtractClick = {
                                        println("DEBUG: Extract clicked for ${app.label}")
                                        permissionLauncher.launch(permissions)
                                        viewModel.extractApk(app)
                                    },
                                    onShareClick = {
                                        println("DEBUG: Share clicked for ${app.label}")
                                        viewModel.shareApk(app)
                                    },
                                    onInfoClick = {
                                        println("DEBUG: Info clicked for ${app.label}")
                                        onNavigateToAppDetail(app.packageName)
                                    },
                                    onOpenPlayStoreClick = { viewModel.openPlayStore(app) },
                                    onLaunchClick = { viewModel.launchApp(app) },
                                    onUninstallClick = { viewModel.uninstallApp(app) },
                                    onCopyPackageNameClick = { viewModel.copyPackageName(app) },
                                    onManifestClick = { viewManifest(app) },
                                    onSaveIconClick = { viewModel.saveIcon(app) },
                                    onShareIconClick = { viewModel.shareIcon(app) }
                                )

                                // Show extraction progress if this app is being extracted
                                extractionProgress?.let { (packageName, progress) ->
                                    if (packageName == app.packageName) {
                                        LinearProgressIndicator(
                                            progress = progress / 100f,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        )
                                    }
                                }

                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
} 