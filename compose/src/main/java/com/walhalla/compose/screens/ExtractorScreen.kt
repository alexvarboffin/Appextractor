package com.walhalla.compose.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.compose.components.AppListItem
import com.walhalla.compose.viewmodel.ExtractorViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import com.walhalla.appextractor.model.PackageMeta
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.ImeAction
import com.walhalla.compose.R
import com.walhalla.compose.detail.SortOption
import com.walhalla.compose.detail.sortMenuItems
import com.walhalla.compose.detail.sortedByOption
import com.walhalla.extractor.R as ExtR

@Composable
fun MainMenu(viewModel: ExtractorViewModel) {
    var showMenu by remember { mutableStateOf(false) }
    var showPlayCategories by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val playCategories = remember {
        listOf(
            "GAME", "FAMILY", "COMMUNICATION", "PRODUCTIVITY", "TOOLS",
            "SOCIAL", "PHOTOGRAPHY", "MUSIC_AND_AUDIO", "VIDEO_PLAYERS", "SHOPPING"
        )
    }

    Box {
        IconButton(onClick = { showMenu = true }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(com.walhalla.extractor.R.string.open_on_google_play)) },
                leadingIcon = { Icon(Icons.Default.Shop, null) },
                onClick = {
                    showMenu = false
                    showPlayCategories = true
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(com.walhalla.extractor.R.string.action_share_app)) },
                leadingIcon = { Icon(Icons.Default.Share, null) },
                onClick = {
                    showMenu = false
                    viewModel.shareApp()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(com.walhalla.extractor.R.string.action_discover_more_app)) },
                leadingIcon = { Icon(Icons.Default.Apps, null) },
                onClick = {
                    showMenu = false
                    viewModel.discoverMoreApps()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(com.walhalla.extractor.R.string.action_rate_app)) },
                leadingIcon = { Icon(Icons.Default.Star, null) },
                onClick = {
                    showMenu = false
                    viewModel.rateApp()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(com.walhalla.extractor.R.string.action_about)) },
                leadingIcon = { Icon(Icons.Default.Info, null) },
                onClick = {
                    showMenu = false
                    viewModel.showAbout()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(com.walhalla.extractor.R.string.action_privacy_policy)) },
                leadingIcon = { Icon(Icons.Default.Security, null) },
                onClick = {
                    showMenu = false
                    viewModel.showPrivacyPolicy()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(com.walhalla.extractor.R.string.action_contact_us)) },
                leadingIcon = { Icon(Icons.Default.Feedback, null) },
                onClick = {
                    showMenu = false
                    viewModel.contactUs()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(com.walhalla.extractor.R.string.action_accessibility)) },
                leadingIcon = { Icon(Icons.Default.Accessibility, null) },
                onClick = {
                    showMenu = false
                    viewModel.showAccessibility()
                }
            )
        }

        if (showPlayCategories) {
            AlertDialog(
                onDismissRequest = { showPlayCategories = false },
                title = { Text(stringResource(com.walhalla.extractor.R.string.open_on_google_play)) },
                text = {
                    Column {
                        playCategories.forEach { category ->
                            TextButton(onClick = {
                                showPlayCategories = false
                                viewModel.openGooglePlayCategory(category)
                            }) {
                                Text(category.replace('_', ' '))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPlayCategories = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
    appVersion: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                )
                
                Column {
                    Text(
                        text = stringResource(R.string.app_name_full),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = stringResource(com.walhalla.extractor.R.string.about_version),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = appVersion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractorScreen(
    viewModel: ExtractorViewModel = viewModel(),
    onNavigateToAppDetail: (PackageMeta) -> Unit,
    viewManifest: (app: PackageMeta) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    LaunchedEffect(activity) {
        viewModel.activityProvider = { activity }
    }
    val apps by viewModel.apps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()
    val extractionProgress by viewModel.extractionProgress.collectAsState()
    val extractionFileCount by viewModel.extractionFileCount.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSearchActive by remember { mutableStateOf(false) }

    val showAboutDialog by viewModel.showAboutDialog.collectAsState()

    var currentSort by remember { mutableStateOf(SortOption.NAME_ASC) }
    val sortedApps = remember(apps, currentSort) { apps.sortedByOption(currentSort) }

    // Debug logging for extraction state
    LaunchedEffect(extractionProgress, extractionFileCount) {
        println("DEBUG: Extraction progress: $extractionProgress")
        println("DEBUG: Extraction file count: $extractionFileCount")
    }

    // Collect from Flow (key = Unit): clearing message must NOT cancel showSnackbar.
    // Previous LaunchedEffect(successMessage) restarted on clear and dropped the snackbar.
    LaunchedEffect(Unit) {
        viewModel.successMessage.collect { message ->
            if (message.isNullOrEmpty()) return@collect
            viewModel.clearSuccessMessage()
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Show extraction progress dialog
    if (extractionProgress != null || extractionFileCount != null) {
        println("DEBUG: Showing extraction dialog")
        AlertDialog(
            onDismissRequest = { /* Can't dismiss during extraction */ },
            title = { Text("Extracting APK") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    extractionFileCount?.let {
                        Text(
                            text = "Extracting ${it.second} file(s)...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (extractionProgress != null) {
                        LinearProgressIndicator(
                            progress = { extractionProgress!!.second / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            text = "${extractionProgress!!.second}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { viewModel.hideAbout() },
            appVersion = viewModel.getAppVersion()
        )
    }

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

    // Outer MainActivity Scaffold already applies bottom nav + system insets.
    // Zero insets here so we don't reserve an empty "banner" strip at the bottom.
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(com.walhalla.extractor.R.string.app_name))
                },
                actions = {
                    SortMenu(currentSort = {
                        currentSort = it
                    })
                    MainMenu(viewModel = viewModel)
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
                // РџРѕРёСЃРєРѕРІР°СЏ СЃС‚СЂРѕРєР°
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    placeholder = { Text(stringResource(com.walhalla.extractor.R.string.search_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { }),
                )

                // Selection bar — quiet strip, not a loud tinted banner
                val selectedCount = selectedApps.size
                val hasSelection = selectedCount > 0
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Column {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(ExtR.string.action_selected),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = selectedCount.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            TextButton(
                                onClick = { viewModel.clearSelection() },
                                enabled = hasSelection,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stringResource(ExtR.string.action_deselect),
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }

                            TextButton(
                                onClick = {
                                    permissionLauncher.launch(permissions)
                                    viewModel.extractSelectedApps()
                                },
                                enabled = hasSelection,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stringResource(ExtR.string.action_extract_btn),
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }

                // Apps list
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        sortedApps.isEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (searchQuery.isBlank()) {
                                        Icons.Default.Apps
                                    } else {
                                        Icons.Default.SearchOff
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (searchQuery.isBlank()) {
                                        stringResource(ExtR.string.data_empty)
                                    } else {
                                        stringResource(
                                            ExtR.string.search_no_results,
                                            searchQuery
                                        )
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        else -> {
                            // Adaptive columns: phone portrait = 1; landscape / tablet = 2+
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 360.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(sortedApps, key = { it.packageName }) { app ->
                                    Column {
                                        AppListItem(
                                            app = app,
                                            isSelected = app in selectedApps,
                                            highlightQuery = searchQuery,
                                            onSelect = {
                                                viewModel.toggleAppSelection(app)
                                            },
                                            onExtractClick = {
                                                permissionLauncher.launch(permissions)
                                                viewModel.extractApkRequest(app)
                                            },
                                            onShareClick = {
                                                viewModel.shareApk(app)
                                            },
                                            onInfoClick = {
                                                onNavigateToAppDetail(app)
                                            },
                                            onOpenPlayStoreClick = { viewModel.openPlayStore(app) },
                                            onLaunchClick = { viewModel.launchApp(app) },
                                            onUninstallClick = { viewModel.uninstallApp(app) },
                                            onCopyPackageNameClick = {
                                                viewModel.copyPackageName(app)
                                            },
                                            onManifestClick = { viewManifest(app) },
                                            onSaveIconClick = { viewModel.saveIcon(app) },
                                            onShareIconClick = { viewModel.shareIcon(app) }
                                        )

                                        extractionProgress?.let { (packageName, progress) ->
                                            if (packageName == app.packageName) {
                                                LinearProgressIndicator(
                                                    progress = progress / 100f,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
}

@Composable
fun SortMenu(currentSort: (sort: SortOption) -> Unit) {
    var showSortMenu by remember { mutableStateOf(false) }
    val items = remember { sortMenuItems() }
    Box {
        IconButton(onClick = { showSortMenu = true }) {
            Icon(
                Icons.Default.Sort,
                contentDescription = stringResource(R.string.sort)
            )
        }

        DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(stringResource(item.labelRes)) },
                    leadingIcon = {
                        Icon(item.icon, contentDescription = null)
                    },
                    onClick = {
                        currentSort(item.option)
                        showSortMenu = false
                    }
                )
            }
        }
    }
}
