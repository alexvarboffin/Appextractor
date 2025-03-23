package com.walhalla.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.walhalla.appextractor.activity.string.MvpContract
import com.walhalla.appextractor.activity.string.StringsPresenter
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.activity.assets.StringItemViewModel

@Composable
fun StringScreen(app: PackageMeta, resourceType: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var stringResources by remember { mutableStateOf<List<StringItemViewModel>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    
    DisposableEffect(app, resourceType) {
        val presenter = StringsPresenter(context, object : MvpContract.View {
            override fun showResourceRawText(resource: StringItemViewModel, content: String) {}
            
            override fun showError(message: String) {
                error = message
            }
            
            override fun showSuccess(list: List<StringItemViewModel>) {
                stringResources = list
            }
            
            override fun successToast(res: Int, aaa: String) {}
            override fun errorToast(res: Int, aaa: String) {}
        })
        
        presenter.loadResourceContent(app.packageName, resourceType)
        
        onDispose {}
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(stringResources) { resource ->
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
                        Text(
                            text = resource.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Row {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(resource.name))
                            }) {
                                Icon(Icons.Default.ContentCopy, "Copy name")
                            }
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(resource.text))
                            }) {
                                Icon(Icons.Default.ContentCopy, "Copy value")
                            }
                            IconButton(onClick = {
                                // TODO: Implement sharing
                            }) {
                                Icon(Icons.Default.Share, "Share")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = resource.text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 