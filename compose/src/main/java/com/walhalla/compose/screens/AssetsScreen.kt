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
import com.walhalla.compose.viewmodel.AssetsViewModel

@Composable
fun AssetsScreen(
    app: PackageMeta,
    viewModel: AssetsViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val assets by viewModel.assets.collectAsState()
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
        items(assets) { asset ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (asset.type == ResType.Dir) {
                            Icon(
                                Icons.Default.Folder,
                                contentDescription = "Folder",
                                modifier = Modifier.size(24.dp)
                            )
                        } else if (asset.drawable != null) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = "Image",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Text(
                            text = asset.fullPath,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (asset.type != ResType.Dir) {
                            Box {
                                IconButton(onClick = { 
                                    selectedAsset = asset
                                    showMenu = true 
                                }) {
                                    Icon(Icons.Default.MoreVert, "More options")
                                }
                                
                                DropdownMenu(
                                    expanded = showMenu && selectedAsset == asset,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    if (!ResItem.isImages(asset)) {
                                        DropdownMenuItem(
                                            text = { Text("Read File") },
                                            leadingIcon = { Icon(Icons.Default.Description, null) },
                                            onClick = {
                                                showMenu = false
                                                viewModel.readAsset(context, asset)
                                            }
                                        )
                                    }
                                    
                                    if (asset.drawable != null) {
                                        DropdownMenuItem(
                                            text = { Text("Save Icon") },
                                            leadingIcon = { Icon(Icons.Default.Save, null) },
                                            onClick = {
                                                showMenu = false
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
                                        )
                                        
                                        DropdownMenuItem(
                                            text = { Text("Share Icon") },
                                            leadingIcon = { Icon(Icons.Default.Share, null) },
                                            onClick = {
                                                showMenu = false
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
                                        )
                                    }
                                    
                                    DropdownMenuItem(
                                        text = { Text("Copy Name") },
                                        leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
                                        onClick = {
                                            showMenu = false
                                            clipboardManager.setText(AnnotatedString(asset.fullPath))
                                        }
                                    )
                                    
//                                    if (asset.text.isNotEmpty()) {
//                                        DropdownMenuItem(
//                                            text = { Text("Copy Content") },
//                                            leadingIcon = { Icon(Icons.Default.ContentCopy, null) },
//                                            onClick = {
//                                                showMenu = false
//                                                clipboardManager.setText(AnnotatedString(asset.text))
//                                            }
//                                        )
//                                    }
                                    
                                    DropdownMenuItem(
                                        text = { Text("Zip All Assets") },
                                        leadingIcon = { Icon(Icons.Default.Archive, null) },
                                        onClick = {
                                            showMenu = false
                                            viewModel.zipAllAssets(context, asset)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
//                    if (asset.text.isNotEmpty() && asset.type != ResType.Dir) {
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(
//                            text = asset.text,
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                    }
                }
            }
        }
    }
}