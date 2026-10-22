package com.walhalla.compose.ui.tv.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Key
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.Icon
import com.walhalla.compose.R
import com.walhalla.compose.tv.LocalTvDrawerFocusRequester
import com.walhalla.compose.ui.tv.components.TvEditableField
import com.walhalla.compose.viewmodel.SettingsViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsState()
    val drawerFocus = LocalTvDrawerFocusRequester.current
    val leftToDrawer = if (drawerFocus != null) {
        Modifier.focusProperties { left = drawerFocus }
    } else Modifier

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.settings_version, settings.appVersion),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ToggleRow(
            title = androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.settings_google_drive_backup),
            enabled = settings.googleDriveEnabled,
            onClick = { viewModel.updateGoogleDriveEnabled(!settings.googleDriveEnabled) },
            modifier = leftToDrawer
        )
        ToggleRow(
            title = androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.settings_dropbox_backup),
            enabled = settings.dropboxEnabled,
            onClick = { viewModel.updateDropboxEnabled(!settings.dropboxEnabled) },
            modifier = leftToDrawer
        )
        ToggleRow(
            title = androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.settings_telegram_backup),
            enabled = settings.telegramEnabled,
            onClick = { viewModel.updateTelegramEnabled(!settings.telegramEnabled) },
            modifier = leftToDrawer
        )
        if (settings.telegramEnabled) {
            FieldLabel(stringResource(R.string.telegram_token))
            TvEditableField(
                value = settings.telegramToken,
                onValueChange = viewModel::updateTelegramToken,
                hint = stringResource(R.string.telegram_token),
                leftFocus = drawerFocus,
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) }
            )
            FieldLabel(stringResource(R.string.telegram_chat_id))
            TvEditableField(
                value = settings.telegramChatId,
                onValueChange = viewModel::updateTelegramChatId,
                hint = stringResource(R.string.telegram_chat_id),
                leftFocus = drawerFocus,
                leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null) }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ToggleRow(
    title: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = if (enabled) androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.settings_on) else androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.settings_off),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
