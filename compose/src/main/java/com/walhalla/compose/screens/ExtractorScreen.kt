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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Update
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import com.walhalla.compose.R
import com.walhalla.extractor.R as ExtR


// Sort options
enum class SortOption {
    NAME_ASC, NAME_DESC,
    PACKAGE_ASC, PACKAGE_DESC,
    SIZE_ASC, SIZE_DESC,
    UPDATE_DATE_ASC, UPDATE_DATE_DESC,
    SYSTEM_FIRST, USER_FIRST
}

@Composable
fun MainMenu(viewModel: ExtractorViewModel) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

    // Sort apps based on current option
    val sortedApps = remember(apps, currentSort) {
        when (currentSort) {
            SortOption.NAME_ASC -> apps.sortedBy { it.label }
            SortOption.NAME_DESC -> apps.sortedByDescending { it.label }
            SortOption.PACKAGE_ASC -> apps.sortedBy { it.packageName }
            SortOption.PACKAGE_DESC -> apps.sortedByDescending { it.packageName }
            SortOption.SIZE_ASC -> apps.sortedBy { it.fileSize }
            SortOption.SIZE_DESC -> apps.sortedByDescending { it.fileSize }
            SortOption.UPDATE_DATE_ASC -> apps.sortedBy { it.updateTime }
            SortOption.UPDATE_DATE_DESC -> apps.sortedByDescending { it.updateTime }
            SortOption.SYSTEM_FIRST -> apps.sortedWith(compareByDescending<PackageMeta> { it.isSystemApp }.thenBy { it.label })
            SortOption.USER_FIRST -> apps.sortedWith(compareBy<PackageMeta> { it.isSystemApp }.thenBy { it.label })
        }
    }

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
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(com.walhalla.extractor.R.string.search_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,

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
                    keyboardActions = KeyboardActions(onDone = { /* Р”РµР№СЃС‚РІРёРµ РїСЂРё РЅР°Р¶Р°С‚РёРё РЅР° Done */ }),
                )

                // Selection bar (full-width, like legacy fragment_main)
                val selectedCount = selectedApps.size
                val hasSelection = selectedCount > 0
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(ExtR.string.action_selected),
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = selectedCount.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
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
                                style = MaterialTheme.typography.bodyMedium,
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
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
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
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(sortedApps) { app ->
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

@Composable
fun SortMenu(currentSort: (sort: SortOption) -> Unit) {
    var showSortMenu by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { showSortMenu = true }) {
            Icon(
                Icons.Default.Sort,
                contentDescription = "Sort apps"
            )
        }

        DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Name (A-Z)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.SortByAlpha,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.NAME_ASC)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Name (Z-A)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.SortByAlpha,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.NAME_DESC)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Package (A-Z)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.PACKAGE_ASC)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Package (Z-A)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.PACKAGE_DESC)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Size (Small-Large)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Storage,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.SIZE_ASC)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Size (Large-Small)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Storage,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.SIZE_DESC)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Update Date (Old-New)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Update,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.UPDATE_DATE_ASC)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Update Date (New-Old)") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Update,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.UPDATE_DATE_DESC)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("System Apps First") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Android,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.SYSTEM_FIRST)
                    showSortMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("User Apps First") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null
                    )
                },
                onClick = {
                    currentSort(SortOption.USER_FIRST)
                    showSortMenu = false
                }
            )
        }
    }
}
