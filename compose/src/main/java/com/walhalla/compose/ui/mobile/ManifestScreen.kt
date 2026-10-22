package com.walhalla.compose.ui.mobile

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import android.view.MotionEvent
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
import androidx.core.net.toUri
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalAnimationApi::class
)
@Composable
fun ManifestScreen(
    app: PackageMeta,
    onBackClick: () -> Unit,
    xmlFileName: String? = null,
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
    val manifestCopiedMsg = androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_copied)

    // Получаем текущий активный манифест
    val currentManifest = manifestStates.getOrNull(pagerState.currentPage)

    fun copyCurrentManifest() {
        currentManifest?.let { state ->
            val textToCopy = if (showFormatted) {
                // Для форматированного вида копируем обычный текст
                state.content
            } else {
                state.content
            }
            clipboardManager.setText(AnnotatedString(textToCopy))
            scope.launch {
                snackbarHostState.showSnackbar(manifestCopiedMsg)
            }
        }
    }

    fun shareCurrentManifest() {
        currentManifest?.let { state ->
            val textToShare = if (showFormatted) {
                state.content
            } else {
                state.content
            }
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                putExtra(Intent.EXTRA_TITLE, context.getString(com.walhalla.compose.R.string.manifest_share_title, app.label, state.name))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }

    LaunchedEffect(app.packageName, xmlFileName) {
        println("DEBUG: ManifestScreen started with package: ${app.packageName} xml=$xmlFileName")
        if (xmlFileName.isNullOrBlank()) {
            viewModel.loadManifests(app)
        } else {
            viewModel.loadXmlResource(app, xmlFileName)
        }
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
            Column {
            TopAppBar(
                    title = { Text(androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.common_back))
                        }
                    },
                    actions = {
                        // Переключатель форматированного вида
                        IconButton(onClick = { showFormatted = !showFormatted }) {
                            Icon(
                                if (showFormatted) Icons.Default.Code else Icons.Default.Article,
                                contentDescription = if (showFormatted) androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_show_plain) else androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_show_formatted)
                            )
                        }

                        // Копировать текущий манифест
                        IconButton(
                            onClick = { copyCurrentManifest() },
                            enabled = currentManifest != null
                        ) {
                            Icon(Icons.Default.ContentCopy, androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_copy))
                        }

                        // Поделиться текущим манифестом
                        IconButton(
                            onClick = { shareCurrentManifest() },
                            enabled = currentManifest != null
                        ) {
                            Icon(Icons.Default.Share, androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_share))
                        }

                        // Открыть настройки приложения
                        IconButton(onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = "package:${app.packageName}".toUri()
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Settings, androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_app_settings))
                        }
                    }
                )

                // Блок с информацией о приложении
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Иконка приложения
                        if (app.icon != null) {
                            AsyncImage(
                                model = app.icon,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        } else {
                            // Показываем плейсхолдер если иконка отсутствует
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Информация о приложении
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = app.label ?: androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.common_unknown),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = app.packageName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
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
                                Text(androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_loading))
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
                            // Добавляем заголовок с информацией о текущем файле
                            val currentState = manifestStates[pagerState.currentPage]
                            Text(
                                text = androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_from, currentState.name),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                            
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
                                        text = { 
                                            Text(
                                                text = state.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1
                                            )
                                        }
                                    )
                                }
                            }

                            HorizontalPager(
                                count = manifestStates.size,
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                val state = manifestStates[page]
                                if (showFormatted) {
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
                                                webViewClient = object : WebViewClient() {
                                                    override fun onPageFinished(view: WebView?, url: String?) {
                                                        super.onPageFinished(view, url)
                                                        println("DEBUG: WebView page finished loading")
                                                    }
                                                }
                                                layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.MATCH_PARENT
                                                )
                                                webView = this
                                                // Pinch-zoom / horizontal pan inside WebView: forbid the parent
                                                // HorizontalPager from intercepting while WebView can still scroll
                                                // in the drag direction or while multi-touch is active.
                                                // At the horizontal edge the pager swipes tabs as before.
                                                var lastX = 0f
                                                @Suppress("ClickableViewAccessibility")
                                                setOnTouchListener { v, event ->
                                                    when (event.actionMasked) {
                                                        MotionEvent.ACTION_DOWN -> {
                                                            lastX = event.x
                                                            v.parent?.requestDisallowInterceptTouchEvent(
                                                                v.canScrollHorizontally(1) || v.canScrollHorizontally(-1)
                                                            )
                                                        }
                                                        MotionEvent.ACTION_POINTER_DOWN ->
                                                            v.parent?.requestDisallowInterceptTouchEvent(true)
                                                        MotionEvent.ACTION_MOVE -> {
                                                            if (event.pointerCount > 1) {
                                                                v.parent?.requestDisallowInterceptTouchEvent(true)
                                                            } else {
                                                                val dx = event.x - lastX
                                                                lastX = event.x
                                                                if (dx != 0f) {
                                                                    // finger right (dx>0) => content scrolls left (-1)
                                                                    val dir = if (dx > 0) -1 else 1
                                                                    v.parent?.requestDisallowInterceptTouchEvent(
                                                                        v.canScrollHorizontally(dir)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                                                            v.parent?.requestDisallowInterceptTouchEvent(false)
                                                    }
                                                    false // let WebView handle zoom/scroll itself
                                                }
                                            }
                                        },
                                        update = { webView ->
                                            println("DEBUG: Updating WebView content for ${state.name}")
                                            if (state.formattedContent != null) {
                                                webView.loadDataWithBaseURL(
                                                    null,
                                                    state.formattedContent!!,
                                                    "text/html",
                                                    "UTF-8",
                                                    null
                                                )
                                            } else {
                                                println("DEBUG: No formatted content available for ${state.name}")
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    ManifestContent(
                                        content = state.content,
                                        onCopyClick = { copyCurrentManifest() }
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
            Icon(Icons.Default.ContentCopy, androidx.compose.ui.res.stringResource(com.walhalla.compose.R.string.manifest_copy))
        }
    }
} 
