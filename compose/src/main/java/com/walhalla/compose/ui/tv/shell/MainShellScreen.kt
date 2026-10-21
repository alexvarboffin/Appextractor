package com.walhalla.compose.ui.tv.shell

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import com.walhalla.compose.tv.LocalTvDrawerFocusRequester
import com.walhalla.compose.ui.tv.components.rememberClearFocusThen
import com.walhalla.compose.ui.tv.components.rememberTvLazyFocusRestorer
import com.walhalla.compose.ui.tv.components.tvLazyFocusGroup
import com.walhalla.compose.ui.tv.extractor.ExtractorScreen
import com.walhalla.compose.ui.tv.logs.LogScreen
import com.walhalla.compose.ui.tv.settings.SettingsScreen
import com.walhalla.compose.viewmodel.ExtractorViewModel

private enum class ShellPanel { Extractor, Logs, Settings }

private data class DrawerItem(
    val id: ShellPanel?,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainShellScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var panel by rememberSaveable { mutableStateOf(ShellPanel.Extractor) }
    var showAbout by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val drawerRestorer = rememberTvLazyFocusRestorer()
    val clearFocusThen = rememberClearFocusThen()
    val activity = LocalContext.current as ComponentActivity
    val extractorVm: ExtractorViewModel = viewModel(viewModelStoreOwner = activity)

    var initialFocusDone by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (initialFocusDone) return@LaunchedEffect
        initialFocusDone = true
        withFrameNanos { }
        // Enter drawer **group** — restorer picks the child (never requestFocus a raw item).
        runCatching { drawerRestorer.parent.requestFocus() }
    }

    fun selectPanel(next: ShellPanel) {
        if (next == panel) return
        // Unpin content Lazy focusRestorer before disposing the panel.
        clearFocusThen { panel = next }
    }

    val drawerItems = listOf(
        DrawerItem(ShellPanel.Extractor, "Extractor", Icons.Default.Storage) {
            selectPanel(ShellPanel.Extractor)
        },
        DrawerItem(ShellPanel.Logs, "Logs", Icons.AutoMirrored.Filled.List) {
            selectPanel(ShellPanel.Logs)
        },
        DrawerItem(ShellPanel.Settings, "Settings", Icons.Default.Settings) {
            selectPanel(ShellPanel.Settings)
        },
        DrawerItem(null, "About", Icons.Default.Info) {
            showAbout = true
        }
    )

    if (showAbout) {
        Dialog(onDismissRequest = { showAbout = false }) {
            Column(
                modifier = Modifier
                    .background(colors.surface)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("ApkChief", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "TV: OK → detail · long-press → actions menu",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = { showAbout = false }) {
                    Text("OK")
                }
            }
        }
    }

    CompositionLocalProvider(LocalTvDrawerFocusRequester provides drawerRestorer.parent) {
        NavigationDrawer(
            drawerState = drawerState,
            modifier = Modifier.fillMaxSize(),
            drawerContent = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(colors.surface)
                        .tvLazyFocusGroup(drawerRestorer),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    itemsIndexed(drawerItems, key = { _, item -> item.label }) { index, item ->
                        NavigationDrawerItem(
                            selected = item.id != null && item.id == panel,
                            onClick = item.onClick,
                            modifier = if (index == 0) drawerRestorer.firstItemModifier() else Modifier,
                            leadingContent = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null
                                )
                            }
                        ) {
                            Text(item.label, maxLines = 1)
                        }
                    }
                }
            }
        ) {
            // No nested focusRestorer here — Lazy lists own Case B restorers.
            // Nesting + TextField Down→child caused LazyLayoutPinnableItem double-release.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
            ) {
                when (panel) {
                    ShellPanel.Extractor -> ExtractorScreen(extractorViewModel = extractorVm)
                    ShellPanel.Logs -> LogScreen()
                    ShellPanel.Settings -> SettingsScreen()
                }
            }
        }
    }
}
