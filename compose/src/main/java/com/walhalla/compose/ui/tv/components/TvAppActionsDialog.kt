package com.walhalla.compose.ui.tv.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.walhalla.compose.R
import com.walhalla.compose.detail.AppMenuActionId
import com.walhalla.compose.detail.appMenuActions

/**
 * Long-click menu for a list item (phone overflow parity) + optional multi-select toggle.
 */
@Composable
fun TvAppActionsDialog(
    appLabel: String,
    onAction: (AppMenuActionId) -> Unit,
    onDismiss: () -> Unit,
    isSelected: Boolean? = null,
    onToggleSelected: () -> Unit = {},
) {
    val actions = remember { appMenuActions() }
    val selectChoice = isSelected?.let { selected ->
        TvChoice(
            label = stringResource(if (selected) R.string.action_unselect else R.string.action_select),
            icon = if (selected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank
        )
    }
    val choices = listOfNotNull(selectChoice) +
        actions.map { TvChoice(stringResource(it.labelRes), it.icon) }
    TvChoiceDialog(
        title = appLabel,
        choices = choices,
        onSelect = { index ->
            if (selectChoice != null && index == 0) {
                onToggleSelected()
            } else {
                onAction(actions[index - if (selectChoice != null) 1 else 0].id)
            }
        },
        onDismiss = onDismiss
    )
}
