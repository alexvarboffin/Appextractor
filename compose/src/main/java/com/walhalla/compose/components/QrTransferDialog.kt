package com.walhalla.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.R
import com.walhalla.compose.transfer.QrTransferController

/** Phone twin of TvQrTransferDialog — same [QrTransferController] / LAN server. */
@Composable
fun QrTransferDialog(controller: QrTransferController) {
    val session = controller.session ?: return
    AlertDialog(
        onDismissRequest = controller::stop,
        title = { Text(stringResource(R.string.qr_title)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (session.errorRes != null) {
                    Text(stringResource(session.errorRes), color = MaterialTheme.colorScheme.error)
                } else {
                    session.qr?.let {
                        Image(
                            bitmap = it,
                            contentDescription = session.url,
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    session.files.forEach { f ->
                        Text(
                            text = "${f.name} · ${prettyFileSize(f.file.length())}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    session.url?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                    }
                    Text(
                        text = stringResource(R.string.qr_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = controller::stop) { Text(stringResource(R.string.qr_stop)) }
        }
    )
}
