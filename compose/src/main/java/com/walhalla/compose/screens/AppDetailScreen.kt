package com.walhalla.compose.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.viewmodel.AppDetailViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.tabs.TabLayout
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: PackageMeta,
    onBackClick: () -> Unit,
    viewModel: AppDetailViewModel = viewModel()
) {
    val appDetails by viewModel.appDetails.collectAsState()

    LaunchedEffect(app) {
        viewModel.loadAppDetails(app)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openInSettings(app) }) {
                        Icon(Icons.Default.Settings, contentDescription = "App Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                ExpandableCard(
                    title = "Basic Information",
                    icon = Icons.Default.Info,
                    initiallyExpanded = true
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Package Name", app.packageName)
                        DetailRow("Version", "${app.versionName} (${app.versionCode})")
                        DetailRow("Size", app.size ?: "Unknown")
                        DetailRow("Install Date", formatDate(app.firstInstallTime))
                        DetailRow("Last Update", formatDate(app.lastUpdateTime))
                        DetailRow("System App", if (app.isSystemApp) "Yes" else "No")
                    }
                }
            }

            appDetails?.let { details ->
                item {
                    ExpandableCard(
                        title = "Permissions (${details.permissions.size})",
                        icon = Icons.Default.Security,
                        initiallyExpanded = false
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            details.permissions.forEach { permission ->
                                DetailRow(
                                    permission.name.substringAfterLast('.'),
                                    permission.description ?: "No description"
                                )
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(
                        title = "Activities (${details.activities.size})",
                        icon = Icons.Default.Apps,
                        initiallyExpanded = false
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            details.activities.forEach { activity ->
                                DetailRow(
                                    activity.name.substringAfterLast('.'),
                                    if (activity.exported) "Exported" else "Not exported"
                                )
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(
                        title = "Services (${details.services.size})",
                        icon = Icons.Default.Build,
                        initiallyExpanded = false
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            details.services.forEach { service ->
                                DetailRow(
                                    service.name.substringAfterLast('.'),
                                    if (service.exported) "Exported" else "Not exported"
                                )
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(
                        title = "Receivers (${details.receivers.size})",
                        icon = Icons.Default.BroadcastOnPersonal,
                        initiallyExpanded = false
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            details.receivers.forEach { receiver ->
                                DetailRow(
                                    receiver.name.substringAfterLast('.'),
                                    if (receiver.exported) "Exported" else "Not exported"
                                )
                            }
                        }
                    }
                }

                item {
                    ExpandableCard(
                        title = "Providers (${details.providers.size})",
                        icon = Icons.Default.Storage,
                        initiallyExpanded = false
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            details.providers.forEach { provider ->
                                DetailRow(
                                    provider.name.substringAfterLast('.'),
                                    if (provider.exported) "Exported" else "Not exported"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableCard(
    title: String,
    icon: ImageVector,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                content()
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        .format(Date(timestamp))
}

@Composable
private fun ManifestTab(app: PackageMeta) {
    ManifestScreen(
        app = app,
        onBackClick = { /* Ignore */ }
    )
}

@Composable
private fun ResourcesTab(app: PackageMeta) {
    // TODO: Implement Resources tab
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Resources coming soon...")
    }
} 