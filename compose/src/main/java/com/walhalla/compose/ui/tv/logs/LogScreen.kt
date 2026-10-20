package com.walhalla.compose.ui.tv.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.walhalla.compose.model.LogModel
import com.walhalla.compose.tv.LocalTvDrawerFocusRequester
import com.walhalla.compose.ui.tv.components.rememberTvLazyFocusRestorer
import com.walhalla.compose.ui.tv.components.tvLazyFocusGroup
import com.walhalla.compose.viewmodel.LogViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LogScreen(viewModel: LogViewModel = viewModel()) {
    val logs by viewModel.logs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val drawerFocus = LocalTvDrawerFocusRequester.current
    val listRestorer = rememberTvLazyFocusRestorer()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val leftToDrawer = if (drawerFocus != null) {
        Modifier.focusProperties { left = drawerFocus }
    } else Modifier

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { viewModel.refresh() },
            modifier = Modifier
                .padding(16.dp)
                .then(leftToDrawer)
        ) {
            Text("Refresh")
        }
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading && logs.isEmpty() -> {
                    Text("Loading…", Modifier.align(Alignment.Center))
                }
                logs.isEmpty() -> {
                    Text("No logs", Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
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
                                onOpen = { /* focusable row; open/share via phone LogItem parity later */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun LogRow(
    log: LogModel,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title: String
    val subtitle: String
    when (log) {
        is LogModel.FileLog -> {
            title = log.fileName
            subtitle = DateFormat.getDateTimeInstance().format(Date(log.lastModified))
        }
        is LogModel.MessageLog -> {
            title = log.message
            subtitle = DateFormat.getDateTimeInstance().format(Date(log.timestamp))
        }
    }
    Surface(
        onClick = onOpen,
        modifier = modifier.fillMaxWidth(),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
