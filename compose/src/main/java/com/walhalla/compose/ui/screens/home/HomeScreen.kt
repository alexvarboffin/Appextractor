package com.walhalla.compose.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage

import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.R

import com.walhalla.extractor.R as coreR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val apps by viewModel.apps.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    // Search
                    IconButton(onClick = { /* TODO: Implement search */ }) {
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }

                    // Sort Menu
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = stringResource(R.string.sort))
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.sort_by_name)) },
                                onClick = { 
                                    /* TODO: Implement sort by name */ 
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.sort_by_install_date)) },
                                onClick = { 
                                    /* TODO: Implement sort by date */ 
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.sort_by_size)) },
                                onClick = { 
                                    /* TODO: Implement sort by size */ 
                                    showSortMenu = false
                                }
                            )
                        }
                    }

                    // Filter Menu
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = stringResource(R.string.filter))
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.show_system_apps)) },
                                onClick = { 
                                    /* TODO: Implement system apps filter */ 
                                    showFilterMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.show_user_apps)) },
                                onClick = { 
                                    /* TODO: Implement user apps filter */ 
                                    showFilterMenu = false
                                }
                            )
                        }
                    }

                    // More Menu
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.refresh)) },
                                onClick = { 
                                    /* TODO: Implement refresh */ 
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.select_all)) },
                                onClick = { 
                                    /* TODO: Implement select all */ 
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.SelectAll, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(coreR.string.backup_selected)) },
                                onClick = { 
                                    /* TODO: Implement backup */ 
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Backup, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(coreR.string.share_selected)) },
                                onClick = { 
                                    /* TODO: Implement share */ 
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(apps) { app ->
                AppItem(
                    app = app,
                    onClick = { onAppClick(app.packageName) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppItem(
    app: PackageMeta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = app.iconUri,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.label.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.version_format, app.versionName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options))
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    // Launch app
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.launch_app)) },
                        onClick = { 
                            /* TODO: Implement launch */ 
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Launch, contentDescription = null)
                        }
                    )
                    
                    // Open in settings
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.app_info)) },
                        onClick = { 
                            /* TODO: Open system app info */ 
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Info, contentDescription = null)
                        }
                    )
                    
                    // Extract APK
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.extract_apk)) },
                        onClick = { 
                            /* TODO: Implement APK extraction */ 
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Download, contentDescription = null)
                        }
                    )
                    
                    // Backup app
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.backup_app)) },
                        onClick = { 
                            /* TODO: Implement backup */ 
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Backup, contentDescription = null)
                        }
                    )
                    
                    // Share APK
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.share_apk)) },
                        onClick = { 
                            /* TODO: Implement share */ 
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Share, contentDescription = null)
                        }
                    )
                    
                    // View manifest
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.view_manifest)) },
                        onClick = { 
                            /* TODO: View manifest */ 
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        }
                    )
                    
                    // View resources
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.view_resources)) },
                        onClick = { 
                            /* TODO: View resources */ 
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Folder, contentDescription = null)
                        }
                    )
                    
                    // View activities
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.view_activities)) },
                        onClick = { 
                            /* TODO: View activities */ 
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.List, contentDescription = null)
                        }
                    )

                    if (app.isSystemApp) {
                        // Disable app
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.disable_app)) },
                            onClick = { 
                                /* TODO: Implement disable */ 
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Block, contentDescription = null)
                            }
                        )
                        
                        // Force stop
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.force_stop)) },
                            onClick = { 
                                /* TODO: Implement force stop */ 
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Stop, contentDescription = null)
                            }
                        )
                    } else {
                        // Uninstall app
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.uninstall)) },
                            onClick = { 
                                /* TODO: Implement uninstall */ 
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
    }
} 