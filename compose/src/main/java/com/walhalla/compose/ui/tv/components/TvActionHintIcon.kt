package com.walhalla.compose.ui.tv.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme

/**
 * Trailing action cue for D-pad: muted when unfocused, primary when focused.
 * Prefer this over "OK · …" text on every card.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvActionHintIcon(
    icon: ImageVector,
    focused: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.size(22.dp),
        tint = if (focused) {
            scheme.primary
        } else {
            scheme.onSurfaceVariant.copy(alpha = 0.45f)
        }
    )
}
