package com.walhalla.compose.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.*
import com.walhalla.compose.viewmodel.ManifestViewModel
import com.walhalla.appextractor.model.PackageMeta
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun ManifestScreen(
    app: PackageMeta,
    onBackClick: () -> Unit,
    viewModel: ManifestViewModel = viewModel()
) {
    println("DEBUG: ManifestScreen started with package: ${app.packageName}")

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val manifestStates by viewModel.manifestStates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showFormatted by remember { mutableStateOf(true) }

    LaunchedEffect(app) {
        println("DEBUG: LaunchedEffect triggered for ${app.packageName}")
        viewModel.loadManifests(app)
    }

    fun showSnackbar(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
        }
    }

    LaunchedEffect(error) {
        error?.let { 
            println("DEBUG: Showing error: $it")
            showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(app.label ?: "Unknown")
                        Text(
                            text = app.packageName,
                            style = MaterialTheme.typography.bodySmall,

                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Переключатель форматированного вида
                    IconButton(onClick = { showFormatted = !showFormatted }) {
                        Icon(
                            if (showFormatted) Icons.Default.Code else Icons.Default.Article,
                            contentDescription = if (showFormatted) "Show plain text" else "Show formatted"
                        )
                    }
                    
                    // Копировать имя пакета
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(app.packageName))
                        showSnackbar("Package name copied")
                    }) {
                        Icon(Icons.Default.ContentCopy, "Copy package name")
                    }
                    
                    // Открыть настройки приложения
                    IconButton(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${app.packageName}")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Settings, "App settings")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = Triple(isLoading, error, manifestStates.isEmpty()),
                transitionSpec = {
                    fadeIn() with fadeOut()
                }
            ) { (loading, err, isEmpty) ->
                when {
                    loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Loading manifest...")
                            }
                        }
                    }
                    err != null -> {
                        Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = err,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    manifestStates.isNotEmpty() -> {
                        Column {
                            if (manifestStates.size > 1) {
                                TabRow(
                                    selectedTabIndex = pagerState.currentPage,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    manifestStates.forEachIndexed { index, state ->
                                        Tab(
                                            selected = pagerState.currentPage == index,
                                            onClick = { 
                                                scope.launch {
                                                    pagerState.animateScrollToPage(index)
                                                }
                                            },
                                            text = { Text(state.name) }
                                        )
                                    }
                                }
                            }
                            
                            HorizontalPager(
                                count = manifestStates.size,
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val state = manifestStates[page]
                                if (showFormatted && state.formattedContent != null) {
                                    var webView by remember { mutableStateOf<WebView?>(null) }
                                    
                                    DisposableEffect(state.apkPath) {
                                        onDispose {
                                            webView?.apply {
                                                clearHistory()
                                                clearCache(true)
                                                loadUrl("about:blank")
                                                onPause()
                                                removeAllViews()
                                                destroy()
                                            }
                                        }
                                    }
                                    
                                    AndroidView(
                                        factory = { context ->
                                            WebView(context).apply {
                                                settings.apply {
                                                    javaScriptEnabled = false
                                                    builtInZoomControls = true
                                                    displayZoomControls = false
                                                    setSupportZoom(true)
                                                    loadWithOverviewMode = true
                                                    useWideViewPort = true
                                                }
                                                webViewClient = WebViewClient()
                                                layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.MATCH_PARENT
                                                )
                                                webView = this
                                            }
                                        },
                                        update = { webView ->
                                            webView.loadDataWithBaseURL(
                                                null,
                                                state.formattedContent?:"NONE",
                                                "text/html",
                                                "UTF-8",
                                                null
                                            )
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    ManifestContent(
                                        content = state.content,
                                        onCopyClick = {
                                            clipboardManager.setText(AnnotatedString(state.content))
                                            showSnackbar("Manifest content copied")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManifestContent(
    content: String,
    onCopyClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
                    SelectionContainer {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            item {
                                Text(
                        text = content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }
                    }
        
        FloatingActionButton(
            onClick = onCopyClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.ContentCopy, "Copy manifest")
        }
    }
} 