package com.walhalla.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.R
import java.text.DateFormat
import java.util.*

@Composable
fun AppListItem(
    app: PackageMeta,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
    onExtractClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onOpenPlayStoreClick: () -> Unit = {},
    onLaunchClick: () -> Unit = {},
    onUninstallClick: () -> Unit = {},
    onCopyPackageNameClick: () -> Unit = {},
    onManifestClick: () -> Unit = {},
    onSaveIconClick: () -> Unit = {},
    onShareIconClick: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .selectable(
                selected = isSelected, onClick = onSelect
            ), colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon


            if (app.icon == null) {//|| app.icon<=0
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

            } else {
                AsyncImage(
                    model = app.icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }


            Spacer(modifier = Modifier.width(8.dp))

            // App Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (app.isSystemApp) {
                        AssistChip(
                            onClick = { },
                            label = { Text("System") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Android,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.height(24.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    if (app.hasPinning) {
                        AssistChip(
                            onClick = { },
                            label = { Text("SSL Pinning") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.PushPin,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.height(24.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                    if (app.hasSplits) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Split APK") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.CallSplit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.height(24.dp),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.Red),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.weight(1f).background(Color.Yellow),
                        text = "v${app.versionName} (${app.versionCode})",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(

                        text = DateFormat.getDateInstance(DateFormat.SHORT).format(Date(app.updateTime)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (app.fileSize > 0) {
                    val x : String = prettyFileSize(app.fileSize)
                    Text(
                        text = "Size: $x", style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, contentDescription = "More options"
                    )
                }

                DropdownMenu(
                    expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    // Extract APK
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_extract)) },
                        leadingIcon = {
                            Icon(Icons.Default.SaveAlt, contentDescription = null)
                        },
                        onClick = {
                            println("DEBUG: Extract menu item clicked")
                            showMenu = false
                            onExtractClick()
                        })

                    // Open in Play Store
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.open_on_google_play)) },
                        leadingIcon = {
                            Icon(Icons.Default.Shop, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onOpenPlayStoreClick()
                        })

                    // Launch App
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_launch_app)) },
                        leadingIcon = {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onLaunchClick()
                        })

                    // Uninstall App
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_uninstall_app)) },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onUninstallClick()
                        })

                    // Copy Package Name
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_copy_package_name)) },
                        leadingIcon = {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onCopyPackageNameClick()
                        })

                    // App Info
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_app_info)) },
                        leadingIcon = {
                            Icon(Icons.Default.Info, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onInfoClick()
                        })

                    // View Manifest
                    DropdownMenuItem(
                        text = { Text(stringResource(com.walhalla.extractor.R.string.action_app_manifest)) },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onManifestClick()
                        })

                    // Save Icon
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_save_icon)) },
                        leadingIcon = {
                            Icon(Icons.Default.Download, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onSaveIconClick()
                        })

                    // Share Icon
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_export_icon)) },
                        leadingIcon = {
                            Icon(Icons.Default.Share, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onShareIconClick()
                        })
                }
            }
        }
    }
}
