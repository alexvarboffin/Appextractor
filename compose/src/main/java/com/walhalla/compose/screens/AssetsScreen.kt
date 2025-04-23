package com.walhalla.compose.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.activity.assets.ResItem
import com.walhalla.appextractor.activity.assets.ResType
import com.walhalla.compose.model.AssetTreeNode
import com.walhalla.compose.viewmodel.AssetsViewModel

@Composable
fun AssetsScreen(
    app: PackageMeta,
    viewModel: AssetsViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val assetTree by viewModel.assetTree.collectAsState()
    val expandedNodes by viewModel.expandedNodes.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedAssetWithContent by viewModel.selectedAsset.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var selectedAsset by remember { mutableStateOf<ResItem?>(null) }
    var lastAction by remember { mutableStateOf<String?>(null) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            selectedAsset?.let { asset ->
                when (lastAction) {
                    "save" -> viewModel.saveIcon(context, asset)
                    "export" -> viewModel.exportIcon(context, asset)
                }
            }
        }
    }
    
    LaunchedEffect(app) {
        viewModel.loadAssets(app.packageName)
    }

    if (error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(error!!)
        }
        return
    }

    if (selectedAssetWithContent != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearSelectedAsset() },
            title = { Text(selectedAssetWithContent!!.first.fullPath) },
            text = { Text(selectedAssetWithContent!!.second) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearSelectedAsset() }) {
                    Text("OK")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(assetTree) { node ->
            AssetTreeItem(
                node = node,
                expandedNodes = expandedNodes,
                onToggleExpansion = { viewModel.toggleNodeExpansion(it) },
                onAssetAction = { asset, action ->
                    when (action) {
                        "read" -> viewModel.readAsset(context, asset)
                        "save" -> {
                            selectedAsset = asset
                            lastAction = "save"
                            if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED) {
                                viewModel.saveIcon(context, asset)
                            } else {
                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        }
                        "export" -> {
                            selectedAsset = asset
                            lastAction = "export"
                            if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED) {
                                viewModel.exportIcon(context, asset)
                            } else {
                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        }
                        "copy" -> clipboardManager.setText(AnnotatedString(asset.fullPath))
                        "zip" -> viewModel.zipAllAssets(context, asset)
                    }
                }
            )
        }
    }
}

@Composable
fun AssetTreeItem(
    node: AssetTreeNode,
    expandedNodes: Set<String>,
    onToggleExpansion: (String) -> Unit,
    onAssetAction: (ResItem, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = (node.level * 16).dp,
                top = 4.dp,
                end = 4.dp,
                bottom = 4.dp
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (node.children.isNotEmpty()) {
                        IconButton(
                            onClick = { onToggleExpansion(node.asset.fullPath) }
                        ) {
                            Icon(
                                if (expandedNodes.contains(node.asset.fullPath)) 
                                    Icons.Default.ExpandMore 
                                else 
                                    Icons.Default.ChevronRight,
                                contentDescription = "Toggle"
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                    
                    Icon(
                        imageVector = when {
                            node.asset.type == ResType.Dir -> Icons.Default.Folder
                            node.asset.drawable != null -> Icons.Default.Image
                            else -> Icons.Default.InsertDriveFile
                        },
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = node.asset.fullPath.substringAfterLast('/'),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                if (node.asset.type != ResType.Dir) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "More options")
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (!ResItem.isImages(node.asset)) {
                                DropdownMenuItem(
                                    text = { Text("Read File") },
                                    leadingIcon = { Icon(Icons.Default.Description, null) },
                                    onClick = {
                                        showMenu = false
                                        onAssetAction(node.asset, "read")
                                    }
                                )
                            }
                            
                            if (node.asset.drawable != null) {
                                DropdownMenuItem(
                                    text = { Text("Save Icon") },
                                    leadingIcon = { Icon(Icons.Default.Save, null) },
                                    onClick = {
                                        showMenu = false
                                        onAssetAction(node.asset, "save")
                                    }
                                )
                                
                                DropdownMenuItem(
                                    text = { Text("Share Icon") },
                                    leadingIcon = { Icon(Icons.Default.Share, null) },
                                    onClick = {
                                        showMenu = false
                                        onAssetAction(node.asset, "export")
                                    }
                                )
                            }
                            
                            DropdownMenuItem(
                                text = { Text("Copy Name") },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
                                onClick = {
                                    showMenu = false
                                    onAssetAction(node.asset, "copy")
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text("Zip All Assets") },
                                leadingIcon = { Icon(Icons.Default.Archive, null) },
                                onClick = {
                                    showMenu = false
                                    onAssetAction(node.asset, "zip")
                                }
                            )
                        }
                    }
                }
            }
            
            if (expandedNodes.contains(node.asset.fullPath)) {
                node.children.forEach { childNode ->
                    AssetTreeItem(
                        node = childNode,
                        expandedNodes = expandedNodes,
                        onToggleExpansion = onToggleExpansion,
                        onAssetAction = onAssetAction,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}