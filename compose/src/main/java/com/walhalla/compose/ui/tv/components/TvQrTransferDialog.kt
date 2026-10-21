package com.walhalla.compose.ui.tv.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.R
import com.walhalla.compose.transfer.QrTransferController

/** Shows the LAN download QR while the server runs; closing the dialog stops sharing. */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvQrTransferDialog(controller: QrTransferController) {
    val session = controller.session ?: return
    val colors = MaterialTheme.colorScheme
    val stopFocus = remember { FocusRequester() }

    Dialog(
        onDismissRequest = controller::stop,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LaunchedEffect(Unit) { runCatching { stopFocus.requestFocus() } }
        Row(
            modifier = Modifier
                .width(860.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(colors.surface)
                .padding(28.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            session.qr?.let { qr ->
                Image(
                    bitmap = qr,
                    contentDescription = session.url,
                    modifier = Modifier
                        .size(320.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.qr_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.onSurface
                )
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (session.errorRes != null) {
                    Text(
                        text = stringResource(session.errorRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.error
                    )
                } else {
                    session.files.forEach { f ->
                        Text(
                            text = "${f.name} · ${prettyFileSize(f.file.length())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    session.url?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = FontFamily.Monospace,
                            color = colors.onSurface
                        )
                    }
                    Text(
                        text = stringResource(R.string.qr_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }
                Button(
                    onClick = controller::stop,
                    modifier = Modifier.focusRequester(stopFocus)
                ) {
                    Text(stringResource(R.string.qr_stop))
                }
            }
        }
    }
}
