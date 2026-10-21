package com.walhalla.compose.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image as BitmapImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.appextractor.activity.assets.ResItem
import com.walhalla.appextractor.activity.assets.ResType
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.model.AssetTreeNode
import com.walhalla.compose.model.flattenVisible
import com.walhalla.compose.viewmodel.AssetsViewModel
import com.walhalla.extractor.R as ExtR

private enum class AssetAction {
    Read, ViewXml, Save, Export, Copy, Zip
}

@Composable
fun AssetsScreen(
    app: PackageMeta,
    onViewXml: (String) -> Unit = {},
    viewModel: AssetsViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val assetTree by viewModel.assetTree.collectAsState()
    val expandedNodes by viewModel.expandedNodes.collectAsState()
    val error by viewModel.error.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val selectedAssetWithContent by viewModel.selectedAsset.collectAsState()
    val imagePreview by viewModel.imagePreview.collectAsState()
    var pendingAsset by remember { mutableStateOf<ResItem?>(null) }
    var pendingAction by remember { mutableStateOf<AssetAction?>(null) }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val asset = pendingAsset
            when (pendingAction) {
                AssetAction.Save -> asset?.let { viewModel.saveIcon(context, it) }
                AssetAction.Export -> asset?.let { viewModel.exportIcon(context, it) }
                else -> Unit
            }
        }
        pendingAsset = null
        pendingAction = null
    }

    fun runAction(asset: ResItem, action: AssetAction) {
        when (action) {
            AssetAction.Read -> viewModel.openAsset(context, asset)
            AssetAction.ViewXml -> onViewXml(asset.fullPath)
            AssetAction.Copy -> clipboardManager.setText(AnnotatedString(asset.fullPath))
            AssetAction.Zip -> viewModel.zipAllAssets(context, asset)
            AssetAction.Save, AssetAction.Export -> {
                pendingAsset = asset
                pendingAction = action
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (action == AssetAction.Save) viewModel.saveIcon(context, asset)
                    else viewModel.exportIcon(context, asset)
                    pendingAsset = null
                    pendingAction = null
                } else {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    LaunchedEffect(app.packageName) {
        viewModel.loadAssets(app.packageName)
    }

    if (error != null && assetTree.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = error!!,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }

    selectedAssetWithContent?.let { (res, content) ->
        AlertDialog(
            onDismissRequest = { viewModel.clearSelectedAsset() },
            title = {
                Text(
                    text = res.fullPath.substringAfterLast('/'),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearSelectedAsset() }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }

    imagePreview?.let { (res, bytes) ->
        val bitmap = remember(bytes) {
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
        AlertDialog(
            onDismissRequest = { viewModel.clearSelectedAsset() },
            title = {
                Text(
                    text = res.fullPath.substringAfterLast('/'),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                if (bitmap == null) {
                    Text(
                        stringResource(com.walhalla.compose.R.string.asset_preview_failed),
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    BitmapImage(
                        bitmap = bitmap,
                        contentDescription = res.fullPath.substringAfterLast('/'),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearSelectedAsset() }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }

    val visibleNodes = remember(assetTree, expandedNodes) {
        assetTree.flattenVisible(expandedNodes)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AssetsToolbar(
            fileCount = visibleNodes.count { !it.isBranch },
            branchCount = visibleNodes.count { it.isBranch },
            onExpandAll = { viewModel.expandAll() },
            onCollapseAll = { viewModel.collapseAll() }
        )
        HorizontalDivider()
        if (assetTree.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No assets",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(
                    items = visibleNodes,
                    key = { it.asset.fullPath }
                ) { node ->
                    AssetTreeRow(
                        node = node,
                        expanded = node.asset.fullPath in expandedNodes,
                        onToggle = { viewModel.toggleNodeExpansion(node.asset.fullPath) },
                        onAction = { action -> runAction(node.asset, action) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetsToolbar(
    fileCount: Int,
    branchCount: Int,
    onExpandAll: () -> Unit,
    onCollapseAll: () -> Unit
) {
    Surface(tonalElevation = 1.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$branchCount folders · $fileCount files shown",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row {
                IconButton(onClick = onExpandAll) {
                    Icon(Icons.Default.UnfoldMore, contentDescription = "Expand all")
                }
                IconButton(onClick = onCollapseAll) {
                    Icon(Icons.Default.UnfoldLess, contentDescription = "Collapse all")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AssetTreeRow(
    node: AssetTreeNode,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAction: (AssetAction) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val indent = (node.level * 16).dp
    val isBranch = node.isBranch

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isBranch) onToggle()
                    else showMenu = true
                },
                onLongClick = { showMenu = true }
            )
            .padding(start = 8.dp + indent, end = 4.dp, top = 2.dp, bottom = 2.dp)
            .height(44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isBranch) {
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onToggle),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }

        Spacer(modifier = Modifier.width(4.dp))

        AssetNodeIcon(node = node, expanded = expanded)

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = node.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!isBranch && node.level > 0) {
                Text(
                    text = node.asset.fullPath.substringBeforeLast('/'),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Actions",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AssetActionsMenu(
                expanded = showMenu,
                asset = node.asset,
                isBranch = isBranch,
                onDismiss = { showMenu = false },
                onAction = {
                    showMenu = false
                    onAction(it)
                }
            )
        }
    }
}

@Composable
private fun AssetNodeIcon(node: AssetTreeNode, expanded: Boolean) {
    val drawable = node.asset.drawable
    if (drawable != null) {
        val bitmap = remember(drawable) {
            runCatching { drawable.toBitmap(width = 48, height = 48) }.getOrNull()
        }
        if (bitmap != null) {
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            return
        }
    }

    val icon: ImageVector = when {
        node.isBranch && expanded -> Icons.Default.FolderOpen
        node.isBranch -> Icons.Default.Folder
        ResItem.isImages(node.asset) || node.asset.type == ResType.Images -> Icons.Default.Image
        node.asset.fullPath.endsWith(".xml", ignoreCase = true) -> Icons.Default.Code
        else -> Icons.AutoMirrored.Filled.InsertDriveFile
    }
    val tint = when {
        node.isBranch -> MaterialTheme.colorScheme.tertiary
        ResItem.isImages(node.asset) -> MaterialTheme.colorScheme.primary
        node.asset.fullPath.endsWith(".xml", ignoreCase = true) -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(22.dp),
        tint = tint
    )
}

@Composable
private fun AssetActionsMenu(
    expanded: Boolean,
    asset: ResItem,
    isBranch: Boolean,
    onDismiss: () -> Unit,
    onAction: (AssetAction) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        if (!isBranch && !ResItem.isImages(asset)) {
            DropdownMenuItem(
                text = { Text(stringResource(ExtR.string.action_read_file)) },
                leadingIcon = { Icon(Icons.Default.Description, null) },
                onClick = { onAction(AssetAction.Read) }
            )
            if (asset.fullPath.endsWith(".xml", ignoreCase = true)) {
                DropdownMenuItem(
                    text = { Text(stringResource(ExtR.string.action_xml_viewer)) },
                    leadingIcon = { Icon(Icons.Default.Code, null) },
                    onClick = { onAction(AssetAction.ViewXml) }
                )
            }
        }
        if (asset.drawable != null) {
            DropdownMenuItem(
                text = { Text(stringResource(ExtR.string.action_save_icon)) },
                leadingIcon = { Icon(Icons.Default.Save, null) },
                onClick = { onAction(AssetAction.Save) }
            )
            DropdownMenuItem(
                text = { Text(stringResource(ExtR.string.action_export_icon)) },
                leadingIcon = { Icon(Icons.Default.Share, null) },
                onClick = { onAction(AssetAction.Export) }
            )
        }
        DropdownMenuItem(
            text = { Text(stringResource(ExtR.string.menu_popup_copy)) },
            leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
            onClick = { onAction(AssetAction.Copy) }
        )
        DropdownMenuItem(
            text = { Text(stringResource(ExtR.string.action_zip_all_assets)) },
            leadingIcon = { Icon(Icons.Default.Archive, null) },
            onClick = { onAction(AssetAction.Zip) }
        )
    }
}
