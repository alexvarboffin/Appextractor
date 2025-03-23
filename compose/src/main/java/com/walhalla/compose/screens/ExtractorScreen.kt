package com.walhalla.compose.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.compose.components.AppListItem
import com.walhalla.compose.viewmodel.ExtractorViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import com.walhalla.appextractor.model.PackageMeta
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import com.walhalla.compose.R


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
    onNavigateToAppDetail: (String) -> Unit,
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

    val successMessage by viewModel.successMessage.collectAsState()
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

    // Show success message
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
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
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                // Поисковая строка
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
                    keyboardActions = KeyboardActions(onDone = { /* Действие при нажатии на Done */ }),
                )

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
                            items(sortedApps) { app ->
                                AppListItem(
                                    app = app,
                                    isSelected = app in selectedApps,
                                    onSelect = {
                                        println("DEBUG: Selection clicked for ${app.label}")
                                        viewModel.toggleAppSelection(app)
                                    },
                                    onExtractClick = {
                                        //@@@@@
                                        println("DEBUG: Extract clicked for ${app.label}")
                                        permissionLauncher.launch(permissions)
                                        viewModel.extractApkRequest(app)
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

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
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
