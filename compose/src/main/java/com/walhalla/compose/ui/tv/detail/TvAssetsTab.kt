package com.walhalla.compose.ui.tv.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.model.flattenVisible
import com.walhalla.compose.viewmodel.AssetsViewModel
import com.walhalla.extractor.R as ExtR

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvAssetsTab(
    app: PackageMeta,
    onViewXml: (String) -> Unit,
    viewModel: AssetsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tree by viewModel.assetTree.collectAsState()
    val expanded by viewModel.expandedNodes.collectAsState()
    val error by viewModel.error.collectAsState()
    val selected by viewModel.selectedAsset.collectAsState()

    LaunchedEffect(app.packageName) {
        viewModel.loadAssets(app.packageName)
    }

    val visible = tree.flattenVisible(expanded)

    when {
        error != null && tree.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
        }
        tree.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(ExtR.string.dlg_loading_title))
        }
        else -> Column(modifier.fillMaxSize()) {
            selected?.let { (res, content) ->
                Surface(
                    onClick = { viewModel.clearSelectedAsset() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(res.fullPath.substringAfterLast('/'), style = MaterialTheme.typography.titleSmall)
                        Text(
                            content.take(800),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 12,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            stringResource(android.R.string.ok) + " · dismiss",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(visible, key = { it.asset.fullPath }) { node ->
                    val indent = (node.level * 16).dp
                    Surface(
                        onClick = {
                            if (node.isBranch) {
                                viewModel.toggleNodeExpansion(node.asset.fullPath)
                            } else if (node.asset.fullPath.endsWith(".xml", ignoreCase = true)) {
                                onViewXml(node.asset.fullPath)
                            } else {
                                viewModel.readAsset(context, node.asset)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        )
                    ) {
                        Row(
                            Modifier.padding(start = indent + 12.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when {
                                    node.isBranch && node.asset.fullPath in expanded -> "▾ "
                                    node.isBranch -> "▸ "
                                    else -> "· "
                                } + node.asset.fullPath.substringAfterLast('/'),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
