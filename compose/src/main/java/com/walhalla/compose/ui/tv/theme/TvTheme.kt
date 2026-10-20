package com.walhalla.compose.ui.tv.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.walhalla.compose.ui.theme.BrandAccent
import com.walhalla.compose.ui.theme.BrandBlue
import com.walhalla.compose.ui.theme.DarkBackground
import com.walhalla.compose.ui.theme.DarkOnSurface
import com.walhalla.compose.ui.theme.DarkOnSurfaceVariant
import com.walhalla.compose.ui.theme.DarkOutline
import com.walhalla.compose.ui.theme.DarkSurface
import com.walhalla.compose.ui.theme.DarkSurfaceVariant

/** Same brand tokens as phone [AppextractorTheme] dark — hex only in `ui.theme.Color`. */
private val TvDarkColors = darkColorScheme(
    primary = BrandBlue,
    onPrimary = DarkOnSurface,
    secondary = BrandBlue.copy(alpha = 0.85f),
    onSecondary = DarkBackground,
    tertiary = BrandAccent,
    onTertiary = DarkOnSurface,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    border = DarkOutline,
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppextractorTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TvDarkColors,
        content = content
    )
}
