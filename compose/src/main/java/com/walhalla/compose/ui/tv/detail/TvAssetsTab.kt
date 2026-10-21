package com.walhalla.compose.ui.tv.detail

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.R
import com.walhalla.compose.model.flattenVisible
import com.walhalla.compose.ui.tv.components.TvTextSectionViewer
import com.walhalla.compose.viewmodel.AssetsViewModel
import com.walhalla.extractor.R as ExtR
import kotlinx.coroutines.delay

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
    val imagePreview by viewModel.imagePreview.collectAsState()

    LaunchedEffect(app.packageName) {
        viewModel.loadAssets(app.packageName)
    }

    val visible = tree.flattenVisible(expanded)

    imagePreview?.let { (res, bytes) ->
        TvAssetPreviewDialog(
            title = res.fullPath.substringAfterLast('/'),
            onDismiss = { viewModel.clearSelectedAsset() }
        ) {
            val bitmap = remember(bytes) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }
            if (bitmap == null) {
                Text(stringResource(R.string.asset_preview_failed), color = MaterialTheme.colorScheme.error)
            } else {
                Image(
                    bitmap = bitmap,
                    contentDescription = res.fullPath.substringAfterLast('/'),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
    selected?.let { (res, content) ->
        TvAssetPreviewDialog(
            title = res.fullPath.substringAfterLast('/'),
            onDismiss = { viewModel.clearSelectedAsset() },
            scrollBody = false,
            focusCloseOnOpen = false
        ) {
            TvTextSectionViewer(
                text = content,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    when {
        error != null && tree.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
        }
        tree.isEmpty() -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(ExtR.string.dlg_loading_title))
        }
        else -> LazyColumn(
                modifier = modifier.fillMaxSize(),
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
                                viewModel.openAsset(context, node.asset)
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvAssetPreviewDialog(
    title: String,
    onDismiss: () -> Unit,
    scrollBody: Boolean = true,
    focusCloseOnOpen: Boolean = true,
    body: @Composable () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val closeFocus = remember { FocusRequester() }
    var armed by remember { mutableStateOf(false) }

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
            armed = true
            if (focusCloseOnOpen) runCatching { closeFocus.requestFocus() }
        }
        Column(
            modifier = Modifier
                .width(680.dp)
                .background(colors.surface)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val kind = fileKind(title)
                Icon(
                    imageVector = kind.first,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = kind.second(colors)
                )
                Text(
                    title,
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (scrollBody) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    body()
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(380.dp)
                        .fillMaxWidth()
                ) {
                    body()
                }
            }
            Button(
                onClick = { if (armed) onDismiss() },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .focusRequester(closeFocus)
            ) {
                Text(stringResource(android.R.string.ok))
            }
        }
    }
}

private val IMAGE_EXT = setOf("png", "jpg", "jpeg", "webp", "gif")
private val CODE_EXT = setOf("xml", "json", "html", "htm", "js", "css")

private fun fileKind(name: String): Pair<ImageVector, (androidx.tv.material3.ColorScheme) -> Color> {
    val ext = name.substringAfterLast('.', "").lowercase()
    return when (ext) {
        in IMAGE_EXT -> Icons.Default.Image to { it.primary }
        in CODE_EXT -> Icons.Default.Code to { it.secondary }
        else -> Icons.AutoMirrored.Filled.InsertDriveFile to { it.onSurfaceVariant }
    }
}
