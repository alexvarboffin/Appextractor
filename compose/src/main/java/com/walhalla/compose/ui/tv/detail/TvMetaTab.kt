package com.walhalla.compose.ui.tv.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.detail.MetaSection
import com.walhalla.compose.detail.loadMetaSections
import com.walhalla.extractor.R as ExtR

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvMetaTab(app: PackageMeta, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var sections by remember(app.packageName) { mutableStateOf<List<MetaSection>>(emptyList()) }
    var loading by remember(app.packageName) { mutableStateOf(true) }

    LaunchedEffect(app.packageName) {
        loading = true
        sections = loadMetaSections(context, app)
        loading = false
    }

    when {
        loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(ExtR.string.dlg_loading_title))
        }
        sections.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.meta_empty),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sections, key = { it.title + it.items.size }) { section ->
                var expanded by remember(section.title) { mutableStateOf(true) }
                Surface(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = if (expanded) "▾ ${section.title}" else "▸ ${section.title}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                if (expanded) {
                    section.items.forEach { line ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(line.key, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                line.value,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 6,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
