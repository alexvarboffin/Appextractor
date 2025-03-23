package com.walhalla.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.HeaderObject
import com.walhalla.appextractor.sdk.MetaPresenter
import com.walhalla.appextractor.activity.detail.DetailsF0

@Composable
fun MetaScreen(app: PackageMeta) {
    val context = LocalContext.current
    var metaData by remember { mutableStateOf<List<BaseViewModel>>(emptyList()) }
    
    DisposableEffect(app) {
        val presenter = MetaPresenter(context, app, object : DetailsF0.View {
            override fun swap(data: List<BaseViewModel>) {
                metaData = data
            }
            //override fun fab() {}
            override fun snack() {}
        })
        
        presenter.doStuff(context)
        
        onDispose {}
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(metaData) { item ->
            when (item) {
                is HeaderObject -> {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                else -> {
                    val line = item.toString()
                    if (line.contains("=")) {
                        val (key, value) = line.split("=", limit = 2)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = key.trim(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = value.trim(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
} 