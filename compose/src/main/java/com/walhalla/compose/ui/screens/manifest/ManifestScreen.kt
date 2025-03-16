package com.walhalla.compose.ui.screens.manifest

import android.content.pm.ActivityInfo
import android.content.pm.ProviderInfo
import android.content.pm.ServiceInfo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.font.FontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManifestScreen(
    packageName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManifestViewModel = viewModel()
) {
    val manifestState by viewModel.manifestState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(ManifestTabType.COMPONENTS) }

    LaunchedEffect(packageName) {
        viewModel.loadManifest(packageName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manifest") },
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
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                ManifestTabType.values().forEach { type ->
                    Tab(
                        selected = selectedTab == type,
                        onClick = { selectedTab = type },
                        text = { Text(type.title) },
                        icon = {
                            Icon(
                                imageVector = when (type) {
                                    ManifestTabType.COMPONENTS -> Icons.Default.List
                                    ManifestTabType.XML -> Icons.Default.Code
                                    ManifestTabType.DECOMPILED -> Icons.Default.Terminal
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                ManifestTabType.COMPONENTS -> {
                    when (val state = manifestState) {
                        is ManifestState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is ManifestState.Error -> {
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
                        is ManifestState.Success -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Permissions Section
                                item {
                                    Text(
                                        text = "Permissions",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(state.permissions) { permission ->
                                    PermissionItem(permission = permission)
                                }

                                // Activities Section
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Activities",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(state.activities) { activity ->
                                    ComponentItem(
                                        name = activity.name,
                                        label = activity.loadLabel(context.packageManager),
                                        exported = activity.exported,
                                        icon = Icons.Default.Launch
                                    )
                                }

                                // Services Section
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Services",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(state.services) { service ->
                                    ComponentItem(
                                        name = service.name,
                                        label = service.loadLabel(context.packageManager),
                                        exported = service.exported,
                                        icon = Icons.Default.Settings
                                    )
                                }

                                // Receivers Section
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Receivers",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(state.receivers) { receiver ->
                                    ComponentItem(
                                        name = receiver.name,
                                        label = receiver.loadLabel(context.packageManager),
                                        exported = receiver.exported,
                                        icon = Icons.Default.Notifications
                                    )
                                }

                                // Providers Section
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Providers",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                items(state.providers) { provider ->
                                    ComponentItem(
                                        name = provider.name,
                                        label = provider.loadLabel(context.packageManager),
                                        exported = provider.exported,
                                        icon = Icons.Default.Storage,
                                        subtitle = provider.authority
                                    )
                                }
                            }
                        }
                    }
                }
                ManifestTabType.XML -> {
                    when (val state = manifestState) {
                        is ManifestState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is ManifestState.Error -> {
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
                        is ManifestState.Success -> {
                            if (state.xmlContent != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = state.xmlContent,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("XML content not available")
                                }
                            }
                        }
                    }
                }
                ManifestTabType.DECOMPILED -> {
                    when (val state = manifestState) {
                        is ManifestState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is ManifestState.Error -> {
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
                        is ManifestState.Success -> {
                            if (state.decompiledContent != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = state.decompiledContent,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Decompiled content not available")
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
private fun PermissionItem(
    permission: String,
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
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = permission,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ComponentItem(
    name: String,
    label: CharSequence?,
    exported: Boolean,
    icon: ImageVector,
    subtitle: String? = null,
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label?.toString() ?: name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (exported) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Exported",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
} 