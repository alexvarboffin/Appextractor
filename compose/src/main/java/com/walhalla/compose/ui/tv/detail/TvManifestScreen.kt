package com.walhalla.compose.ui.tv.detail

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.compose.viewmodel.ManifestViewModel
import com.walhalla.extractor.R as ExtR

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvManifestScreen(
    app: PackageMeta,
    xmlFileName: String?,
    onBack: () -> Unit,
    viewModel: ManifestViewModel = viewModel()
) {
    val states by viewModel.manifestStates.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val backFocus = remember { FocusRequester() }

    LaunchedEffect(app.packageName, xmlFileName) {
        if (xmlFileName.isNullOrBlank()) {
            viewModel.loadManifests(app)
        } else {
            viewModel.loadXmlResource(app, xmlFileName)
        }
    }

    BackHandler(onBack = onBack)

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier
                .focusRequester(backFocus)
                .padding(bottom = 12.dp)
        ) {
            Text(stringResource(ExtR.string.action_app_manifest) + " · Back")
        }

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(ExtR.string.dlg_loading_title))
            }
            error != null -> Text(error.orEmpty(), color = MaterialTheme.colorScheme.error)
            else -> LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(states) { state ->
                    Column(Modifier.fillMaxWidth()) {
                        Text(state.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = state.formattedContent ?: state.content,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        runCatching { backFocus.requestFocus() }
    }
}
