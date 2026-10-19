package com.walhalla.compose.components

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.walhalla.appextractor.utils.Util
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.model.LogModel
import com.walhalla.compose.model.LogType
import com.walhalla.extractor.R as ExtR
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogItem(
    log: LogModel,
    onDeleteClick: ((String) -> Unit)? = null
) {
    when (log) {
        is LogModel.FileLog -> FileLogRow(log = log, onDeleteClick = onDeleteClick)
        is LogModel.MessageLog -> MessageLogRow(log = log)
    }
}

@Composable
private fun FileLogRow(
    log: LogModel.FileLog,
    onDeleteClick: ((String) -> Unit)?
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val dateFormat = remember {
        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    }

    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.fileName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = prettyFileSize(log.size),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (log.lastModified > 0) {
                            Text(
                                text = dateFormat.format(Date(log.lastModified)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(ExtR.string.action_open_file_parent_folder))
                            },
                            leadingIcon = {
                                Icon(Icons.Default.FolderOpen, contentDescription = null)
                            },
                            onClick = {
                                showMenu = false
                                Util.openFolder(context, File(log.filePath).parent)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(ExtR.string.action_share_file)) },
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null)
                            },
                            onClick = {
                                showMenu = false
                                shareApk(context, File(log.filePath))
                            }
                        )
                        if (onDeleteClick != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(ExtR.string.action_delete_file))
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick(log.filePath)
                                }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun MessageLogRow(log: LogModel.MessageLog) {
    val dateFormat = remember {
        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    }
    val (container, content, icon) = messageStyle(log.type)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = container,
        contentColor = content
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (log.type != LogType.Empty) {
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = content.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun messageStyle(type: LogType): Triple<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color, ImageVector> {
    val scheme = MaterialTheme.colorScheme
    return when (type) {
        LogType.Success -> Triple(
            scheme.tertiaryContainer,
            scheme.onTertiaryContainer,
            Icons.Default.CheckCircle
        )
        LogType.Error -> Triple(
            scheme.errorContainer,
            scheme.onErrorContainer,
            Icons.Default.Error
        )
        LogType.Empty -> Triple(
            scheme.surface,
            scheme.onSurfaceVariant,
            Icons.Default.Inbox
        )
        LogType.Warning -> Triple(
            scheme.secondaryContainer,
            scheme.onSecondaryContainer,
            Icons.Default.Info
        )
        LogType.Info -> Triple(
            scheme.surfaceVariant,
            scheme.onSurfaceVariant,
            Icons.Default.Info
        )
    }
}

private fun shareApk(context: android.content.Context, file: File) {
    if (!file.exists() || !file.canRead()) return
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(intent, context.getString(ExtR.string.action_share_file))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (_: Exception) {
        // FileProvider / no handler
    }
}
