package com.walhalla.compose.ui.tv.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.walhalla.compose.detail.AppMenuAction
import com.walhalla.compose.detail.AppMenuActionId
import com.walhalla.compose.detail.appMenuActions
import kotlinx.coroutines.delay

/**
 * Long-click menu for a list item (FireTv pattern): arm delay so KeyUp of the
 * long-press does not fire the first focused button.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvAppActionsDialog(
    appLabel: String,
    onAction: (AppMenuActionId) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val cancelFocus = remember { FocusRequester() }
    var actionsArmed by remember { mutableStateOf(false) }
    val actions = remember { appMenuActions() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        LaunchedEffect(Unit) {
            delay(320)
            actionsArmed = true
            runCatching { cancelFocus.requestFocus() }
        }
        Column(
            modifier = Modifier
                .width(520.dp)
                .heightIn(max = 520.dp)
                .background(colors.surface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = appLabel,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.onSurface
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                itemsIndexed(actions, key = { _, a -> a.id }) { _, action ->
                    Button(
                        onClick = {
                            if (actionsArmed) {
                                onAction(action.id)
                                onDismiss()
                            }
                        },
                        enabled = actionsArmed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DialogActionLabel(action)
                    }
                }
            }
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(cancelFocus)
            ) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DialogActionLabel(action: AppMenuAction) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(stringResource(action.labelRes))
    }
}
