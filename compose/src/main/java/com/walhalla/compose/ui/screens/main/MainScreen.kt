package com.walhalla.compose.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.walhalla.compose.ui.navigation.MainTabType
import com.walhalla.compose.ui.screens.home.HomeScreen
import com.walhalla.compose.ui.screens.settings.SettingsScreen
import com.walhalla.compose.ui.screens.log.LogScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(MainTabType.APPLICATIONS) }
    var showMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AppExtractor") },
                actions = {
                    // Search Icon
                    IconButton(onClick = { /* TODO: Implement search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }

                    // Sort Menu
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("By name") },
                                onClick = { 
                                    /* TODO: Implement sort by name */ 
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("By install date") },
                                onClick = { 
                                    /* TODO: Implement sort by date */ 
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("By size") },
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
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Show system apps") },
                                onClick = { 
                                    /* TODO: Implement system apps filter */ 
                                    showFilterMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Show user apps") },
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
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Refresh") },
                                onClick = { 
                                    /* TODO: Implement refresh */ 
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Select all") },
                                onClick = { 
                                    /* TODO: Implement select all */ 
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.SelectAll, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Backup selected") },
                                onClick = { 
                                    /* TODO: Implement backup */ 
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Backup, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share selected") },
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                MainTabType.values().forEach { type ->
                    Tab(
                        selected = selectedTab == type,
                        onClick = { selectedTab = type },
                        text = { Text(type.title) },
                        icon = {
                            Icon(
                                imageVector = when (type) {
                                    MainTabType.APPLICATIONS -> Icons.Default.Apps
                                    MainTabType.SETTINGS -> Icons.Default.Settings
                                    MainTabType.LOG -> Icons.Default.List
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                MainTabType.APPLICATIONS -> {
                    HomeScreen(
                        onAppClick = onAppClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                MainTabType.SETTINGS -> {
                    SettingsScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                MainTabType.LOG -> {
                    LogScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
} 