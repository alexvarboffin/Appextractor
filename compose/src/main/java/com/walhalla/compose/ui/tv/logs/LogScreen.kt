package com.walhalla.compose.ui.tv.logs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.walhalla.appextractor.utils.Util
import com.walhalla.appextractor.utils.prettyFileSize
import com.walhalla.compose.R
import com.walhalla.compose.log.AppLogBus
import com.walhalla.compose.log.shareApkFile
import com.walhalla.compose.model.LogModel
import com.walhalla.compose.model.LogType
import com.walhalla.compose.transfer.SharedFile
import com.walhalla.compose.transfer.rememberQrTransfer
import com.walhalla.compose.tv.LocalTvDrawerFocusRequester
import com.walhalla.compose.ui.tv.components.RestoreFocusOnReturn
import com.walhalla.compose.ui.tv.components.TvChoice
import com.walhalla.compose.ui.tv.components.TvChoiceDialog
import com.walhalla.compose.ui.tv.components.TvQrTransferDialog
import com.walhalla.compose.ui.tv.components.rememberEnterListFocus
import com.walhalla.compose.ui.tv.components.rememberTvLazyFocusRestorer
import com.walhalla.compose.ui.tv.components.tvLazyFocusGroup
import com.walhalla.compose.viewmodel.LogViewModel
import com.walhalla.extractor.R as ExtR
import java.io.File
import java.text.DateFormat
import java.util.Date

private enum class FileLogAction(val labelRes: Int, val icon: ImageVector) {
    OpenFolder(ExtR.string.action_open_file_parent_folder, Icons.Default.FolderOpen),
    Share(ExtR.string.action_share_file, Icons.Default.Share),
    SendQr(R.string.action_send_qr, Icons.Default.QrCode2),
    Delete(ExtR.string.action_delete_file, Icons.Default.Delete),
}

/** TV Logs: same actions as phone [com.walhalla.compose.components.LogItem] + QR transfer. */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LogScreen(viewModel: LogViewModel = viewModel()) {
    val logs by viewModel.logs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val drawerFocus = LocalTvDrawerFocusRequester.current
    val listRestorer = rememberTvLazyFocusRestorer()
    val listState = rememberLazyListState()
    val enterList = rememberEnterListFocus(listRestorer, listState, logs.size)
    val lifecycleOwner = LocalLifecycleOwner.current
    val qrTransfer = rememberQrTransfer()
    var fileMenu by remember { mutableStateOf<LogModel.FileLog?>(null) }

    listRestorer.RestoreFocusOnReturn(listState, logs.size)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fileMenu?.let { log ->
        val actions = FileLogAction.entries
        TvChoiceDialog(
            title = log.fileName,
            choices = actions.map { TvChoice(stringResource(it.labelRes), it.icon) },
            onSelect = { index ->
                val file = File(log.filePath)
                when (actions[index]) {
                    FileLogAction.OpenFolder -> Util.openFolder(context, file.parent)
                    FileLogAction.Share -> shareApkFile(context, file)
                    FileLogAction.SendQr -> qrTransfer.start(log.fileName, listOf(SharedFile(file, file.name)))
                    FileLogAction.Delete -> viewModel.deleteFile(log.filePath)
                }
            },
            onDismiss = { fileMenu = null }
        )
    }

    TvQrTransferDialog(qrTransfer)

    val leftToDrawer = if (drawerFocus != null) {
        Modifier.focusProperties { left = drawerFocus }
    } else Modifier
    // Scroll-then-focus — never focusProperties { down = list.group } (uninit child crash).
    val downToList = Modifier.onPreviewKeyEvent { event ->
        if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown && logs.isNotEmpty()) {
            enterList()
            true
        } else {
            false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.logs_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = 12.dp)
            )
            Button(
                onClick = { viewModel.refresh() },
                modifier = leftToDrawer.then(downToList)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.logs_refresh))
            }
            Button(
                onClick = { viewModel.clearSessionMessages() },
                modifier = downToList
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.logs_clear))
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && logs.isEmpty()) {
                Text(stringResource(ExtR.string.dlg_loading_title), Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .tvLazyFocusGroup(listRestorer)
                        .then(leftToDrawer),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(logs) { index, log ->
                        LogRow(
                            log = log,
                            modifier = listRestorer.itemModifier(index, logs.size),
                            onOpen = {
                                when (log) {
                                    is LogModel.FileLog -> fileMenu = log
                                    is LogModel.MessageLog -> copyMessage(context, log.message)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun copyMessage(context: Context, message: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("log", message))
    AppLogBus.info(context.getString(ExtR.string.copied_to_clipboard))
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun LogRow(
    log: LogModel,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val dateFormat = remember { DateFormat.getDateTimeInstance() }
    val title: String
    val subtitle: String
    val icon: ImageVector
    val accent: Color
    val hint: ImageVector
    when (log) {
        is LogModel.FileLog -> {
            title = log.fileName
            subtitle = "${prettyFileSize(log.size)} · ${dateFormat.format(Date(log.lastModified))}"
            icon = Icons.Default.Android
            accent = colors.primary
            hint = Icons.Default.Share
        }
        is LogModel.MessageLog -> {
            title = log.message
            subtitle = dateFormat.format(Date(log.timestamp))
            hint = Icons.Default.ContentCopy
            when (log.type) {
                LogType.Success -> { icon = Icons.Default.CheckCircle; accent = colors.tertiary }
                LogType.Error -> { icon = Icons.Default.Error; accent = colors.error }
                LogType.Empty -> { icon = Icons.Default.Inbox; accent = colors.onSurfaceVariant }
                LogType.Warning -> { icon = Icons.Default.Info; accent = colors.secondary }
                LogType.Info -> { icon = Icons.Default.Info; accent = colors.onSurfaceVariant }
            }
        }
    }
    Surface(
        onClick = onOpen,
        modifier = modifier.fillMaxWidth(),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = colors.surface,
            focusedContainerColor = colors.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (log is LogModel.FileLog) FontWeight.SemiBold else null,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
            }
            Icon(
                hint,
                contentDescription = null,
                tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
