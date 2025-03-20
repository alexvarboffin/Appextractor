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
import androidx.compose.ui.unit.dp
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.PermissionUtils
import com.walhalla.compose.viewmodel.AppDetailViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*
import android.content.pm.PermissionInfo

import com.walhalla.compose.model.PermissionCategory
import com.walhalla.compose.model.SecurityLevel
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.walhalla.appextractor.utils.prettyFileSize


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
                    val x = prettyFileSize(app.fileSize)
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Package Name", app.packageName)
                        DetailRow("Version", "${app.versionName} (${app.versionCode})")
                        DetailRow("Size", x)
                        DetailRow("Install Date", formatDate(app.firstInstallTime))
                        DetailRow("Last Update", formatDate(app.lastUpdateTime))
                        DetailRow("System App", if (app.isSystemApp) "Yes" else "No")
                    }
                }
            }

            appDetails?.let { details ->
                item {
                    ExpandableCard(title = "Разрешения", icon = Icons.Default.Security, initiallyExpanded = false
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            details.permissions.forEach { (category, permissions) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (category.securityLevel) {
                                            SecurityLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
                                            SecurityLevel.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                                            SecurityLevel.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                                        }
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = category.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = when (category.securityLevel) {
                                                    SecurityLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                                                    SecurityLevel.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                                                    SecurityLevel.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
                                                }
                                            )
                                            Text(
                                                text = "${permissions.size}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = when (category.securityLevel) {
                                                    SecurityLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                                                    SecurityLevel.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                                                    SecurityLevel.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
                                                }
                                            )
                                        }

                                        Text(
                                            text = category.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = when (category.securityLevel) {
                                                SecurityLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                                                SecurityLevel.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                                                SecurityLevel.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
                                            }
                                        )

                                        Text(
                                            text = "Уровень безопасности: ${category.securityLevel.title}",
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(bottom = 8.dp),
                                            color = when (category.securityLevel) {
                                                SecurityLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
                                                SecurityLevel.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                                                SecurityLevel.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
                                            }
                                        )

                                        permissions.forEach { permission ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surface
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = when (category) {


                                                                PermissionCategory.PERSONAL_DATA -> Icons.Default.Person
                                                                PermissionCategory.LOCATION -> Icons.Default.LocationOn
                                                                PermissionCategory.STORAGE -> Icons.Default.Storage
                                                                PermissionCategory.CAMERA_MIC -> Icons.Default.PhotoCamera
                                                                PermissionCategory.PHONE -> Icons.Default.Phone
                                                                PermissionCategory.NETWORK -> Icons.Default.Wifi
                                                                PermissionCategory.BLUETOOTH -> Icons.Default.Bluetooth
                                                                PermissionCategory.NOTIFICATIONS -> Icons.Default.Notifications
                                                                PermissionCategory.SYSTEM -> Icons.Default.Settings
                                                                PermissionCategory.OTHER -> Icons.Default.Info

                                                            },
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                        
                                                        Column {
                                                            Text(
                                                                text = permission.simpleName,
                                                                style = MaterialTheme.typography.titleSmall
                                                            )
                                                            Row(
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Icon(
                                                                    imageVector = if (permission.isGranted) 
                                                                        Icons.Default.CheckCircle 
                                                                    else 
                                                                        Icons.Default.Cancel,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(16.dp),
                                                                    tint = if (permission.isGranted)
                                                                        MaterialTheme.colorScheme.primary
                                                                    else
                                                                        MaterialTheme.colorScheme.error
                                                                )
                                                                
                                                                val context = LocalContext.current
                                                                Icon(
                                                                    imageVector = if (permission.isGranted) 
                                                                        Icons.Default.LockOpen
                                                                    else 
                                                                        Icons.Default.Lock,
                                                                    contentDescription = "Protection Level",
                                                                    modifier = Modifier
                                                                        .size(16.dp)
                                                                        .clickable {
                                                                            Toast.makeText(
                                                                                context,
                                                                                """
                                                                                Is Permission Granted? ${permission.isGranted}
                                                                                Permission ProtectionLevel: ${permission.protectionLevel}
                                                                                """.trimIndent(),
                                                                                Toast.LENGTH_LONG
                                                                            ).show()
                                                                        },
                                                                    tint = when (permission.protectionLevel) {
                                                                        PermissionInfo.PROTECTION_DANGEROUS -> MaterialTheme.colorScheme.error
                                                                        PermissionInfo.PROTECTION_SIGNATURE -> MaterialTheme.colorScheme.primary
                                                                        else -> MaterialTheme.colorScheme.secondary
                                                                    }
                                                                )
                                                                
                                                                Text(
                                                                    text = PermissionUtils.protectionLevelToString(permission.protectionLevel),
                                                                    style = MaterialTheme.typography.labelSmall,
                                                                    color = MaterialTheme.colorScheme.primary
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (permission.description.isNotBlank()) {
                                                        Text(
                                                            text = permission.description,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            modifier = Modifier.padding(start = 32.dp, top = 4.dp)
                                                        )
                                                    }

                                                    Card(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 32.dp, top = 8.dp),
                                                        colors = CardDefaults.cardColors(
                                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                                        )
                                                    ) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(8.dp),
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Info,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                            Text(
                                                                text = permission.recommendation,
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
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