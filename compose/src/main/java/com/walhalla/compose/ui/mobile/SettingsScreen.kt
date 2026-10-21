package com.walhalla.compose.ui.mobile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.walhalla.compose.R
import com.walhalla.compose.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()
    var showAbout by remember { mutableStateOf(false) }

    if (showAbout) {
        AboutDialog(
            onDismiss = { showAbout = false },
            appVersion = settings.appVersion
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Storage Settings
            Text(
                text = "Storage Settings",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Google Drive
            ListItem(
                headlineContent = { Text("Google Drive Backup") },
                trailingContent = {
                    Switch(
                        checked = settings.googleDriveEnabled,
                        onCheckedChange = { viewModel.updateGoogleDriveEnabled(it) }
                    )
                }
            )

            // Dropbox
            ListItem(
                headlineContent = { Text("Dropbox Backup") },
                trailingContent = {
                    Switch(
                        checked = settings.dropboxEnabled,
                        onCheckedChange = { viewModel.updateDropboxEnabled(it) }
                    )
                }
            )

            // Telegram Settings
            Text(
                text = "Telegram Settings",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            ListItem(
                headlineContent = { Text("Enable Telegram") },
                trailingContent = {
                    Switch(
                        checked = settings.telegramEnabled,
                        onCheckedChange = { viewModel.updateTelegramEnabled(it) }
                    )
                }
            )

            if (settings.telegramEnabled) {
                var tokenText by remember { mutableStateOf(settings.telegramToken) }
                var chatIdText by remember { mutableStateOf(settings.telegramChatId) }

                OutlinedTextField(
                    value = tokenText,
                    onValueChange = {
                        tokenText = it
                        viewModel.updateTelegramToken(it)
                    },
                    label = { Text(stringResource(R.string.telegram_token)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                OutlinedTextField(
                    value = chatIdText,
                    onValueChange = {
                        chatIdText = it
                        viewModel.updateTelegramChatId(it)
                    },
                    label = { Text(stringResource(R.string.telegram_chat_id)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            // About Section
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            ListItem(
                headlineContent = { Text(settings.appName) },
                supportingContent = { Text("Version ${settings.appVersion}") },
                modifier = Modifier.clickable { showAbout = true }
            )

            // Additional Info
            Text(
                text = "App Extractor is a tool for extracting APK files from installed applications.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
} 