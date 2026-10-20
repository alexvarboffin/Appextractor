package com.walhalla.compose.ui.tv.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val TvDarkColors = darkColorScheme(
    primary = Color(0xFF5C8FED),
    onPrimary = Color.White,
    secondary = Color(0xFF8AADE8),
    background = Color(0xFF0E1116),
    onBackground = Color(0xFFE8EDF2),
    surface = Color(0xFF1A222D),
    onSurface = Color(0xFFE8EDF2),
    surfaceVariant = Color(0xFF2A3340),
    onSurfaceVariant = Color(0xFFA8B3C0),
    border = Color(0xFF3D4856),
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppextractorTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TvDarkColors,
        content = content
    )
}
