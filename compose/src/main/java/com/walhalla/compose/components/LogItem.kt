package com.walhalla.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.walhalla.compose.model.LogModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogItem(
    log: LogModel,
    onDeleteClick: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                painter = painterResource(id = when (log) {
                    is LogModel.FileLog -> log.icon
                    is LogModel.MessageLog -> log.icon
                }),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                when (log) {
                    is LogModel.FileLog -> {
                        Text(
                            text = log.fileName,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Size: ${formatFileSize(log.size)}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Modified: ${formatDate(log.lastModified)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    is LogModel.MessageLog -> {
                        Text(
                            text = log.message,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = formatDate(log.timestamp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Delete button for files
            if (log is LogModel.FileLog && onDeleteClick != null) {
                IconButton(onClick = { onDeleteClick(log.filePath) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete file"
                    )
                }
            }
        }
    }
}

private fun formatFileSize(size: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB")
    var value = size.toDouble()
    var unitIndex = 0
    while (value > 1024 && unitIndex < units.size - 1) {
        value /= 1024
        unitIndex++
    }
    return String.format("%.1f %s", value, units[unitIndex])
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
} 