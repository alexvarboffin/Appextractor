package com.walhalla.compose.ui.screens.resources

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    packageName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResourcesViewModel = viewModel()
) {
    val resourcesState by viewModel.resourcesState.collectAsState()
    var selectedTab by remember { mutableStateOf(ResourceType.STRINGS) }

    LaunchedEffect(packageName) {
        viewModel.loadResources(packageName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resources") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Resource Type Tabs
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                ResourceType.values().forEach { type ->
                    Tab(
                        selected = selectedTab == type,
                        onClick = { selectedTab = type },
                        text = { Text(type.title) },
                        icon = {
                            Icon(
                                imageVector = when (type) {
                                    ResourceType.STRINGS -> Icons.Default.TextFields
                                    ResourceType.DRAWABLES -> Icons.Default.Image
                                    ResourceType.LAYOUTS -> Icons.Default.ViewModule
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            when (val state = resourcesState) {
                is ResourcesState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ResourcesState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is ResourcesState.Success -> {
                    when (selectedTab) {
                        ResourceType.STRINGS -> StringsList(strings = state.resources.strings)
                        ResourceType.DRAWABLES -> DrawablesList(drawables = state.resources.drawables)
                        ResourceType.LAYOUTS -> LayoutsList(layouts = state.resources.layouts)
                    }
                }
            }
        }
    }
}

@Composable
private fun StringsList(
    strings: Map<String, String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(strings.entries.toList()) { (key, value) ->
            StringResourceItem(key = key, value = value)
        }
    }
}

@Composable
private fun DrawablesList(
    drawables: Map<String, Any>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(drawables.entries.toList()) { (key, value) ->
            DrawableResourceItem(key = key, value = value)
        }
    }
}

@Composable
private fun LayoutsList(
    layouts: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(layouts) { layout ->
            LayoutResourceItem(name = layout)
        }
    }
}

@Composable
private fun StringResourceItem(
    key: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = key,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DrawableResourceItem(
    key: String,
    value: Any,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = value,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = key,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LayoutResourceItem(
    name: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ViewModule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
} 